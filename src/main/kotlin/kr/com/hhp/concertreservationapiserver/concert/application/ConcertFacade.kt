package kr.com.hhp.concertreservationapiserver.concert.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.concert.controller.ConcertDto
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertDetailService
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertReservationHistoryService
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertSeatPaymentHistoryService
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertSeatService
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertService
import kr.com.hhp.concertreservationapiserver.token.domain.service.TokenQueueService
import kr.com.hhp.concertreservationapiserver.wallet.domain.service.WalletHistoryService
import kr.com.hhp.concertreservationapiserver.wallet.domain.service.WalletService
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Facade
class ConcertFacade(
    private val tokenQueueService: TokenQueueService,
    private val walletService: WalletService,
    private val concertService: ConcertService,
    private val concertDetailService: ConcertDetailService,
    private val concertSeatService: ConcertSeatService,
    private val concertReservationHistoryService: ConcertReservationHistoryService,
    private val concertSeatPaymentHistoryService: ConcertSeatPaymentHistoryService,
    private val walletHistoryService: WalletHistoryService
) {

    // 예약 가능 날짜 조회
    @Transactional(readOnly = true)
    fun getAllAvailableReservationDetail(token:String, concertId: Long, reservationDateTime: LocalDateTime): List<ConcertDto.DetailResponse> {
        // 실제 존재하는 concertId가 맞는지 확인
        val concert = concertService.getByConcertId(concertId)
        val availableReservationConcertDetails = concertDetailService.getAllAvailableReservationByConcertId(
            concertId = concert.concertId!!,
            reservationDateTime = reservationDateTime
        )

        return availableReservationConcertDetails
            .map {
                ConcertDto.DetailResponse(
                    concertId = it.concertId,
                    totalSeatCount = it.totalSeatCount,
                    remainingSeatCount = it.remainingSeatCount,
                    reservationStartDateTime = it.reservationStartDateTime,
                    reservationEndDateTime = it.reservationEndDateTime,
                )
            }
    }

    // 예약 가능 좌석 조회
    @Transactional(readOnly = true)
    fun getAllAvailableReservationSeat(token:String, concertDetailId: Long): List<ConcertDto.SeatResponse> {
        val concertDetail = concertDetailService.getByConcertDetailId(concertDetailId)

        return concertSeatService.getAllReservationStatusIsAvailableByConcertDetailId(concertDetail.concertDetailId!!)
            .map { ConcertDto.SeatResponse(
                concertSeatId = it.concertSeatId!!,
                seatNumber = it.seatNumber,
                price = it.price,
                reservationStatus = it.reservationStatus.toString()
            ) }
    }

    // 좌석 임시 예약
    @Transactional
    fun reserveSeatToTemporary(token: String, concertSeatId: Long): ConcertDto.SeatResponse {
        val tokenQueue = tokenQueueService.getByToken(token)

        val concertSeat = concertSeatService.getByConcertSeatId(concertSeatId)
        val concertDetail = concertDetailService.getByConcertDetailId(concertSeat.concertDetailId)
        concertDetailService.throwExceptionIfNotReservationPeriod(concertDetail)

        val updatedConcertSeat = concertSeatService.reserveSeatToTemporary(
            concertSeatId = concertSeatId,
            userId = tokenQueue.userId
        )
        concertReservationHistoryService.create(
            concertSeatId = updatedConcertSeat.concertSeatId!!,
            status = updatedConcertSeat.reservationStatus
        )

        return ConcertDto.SeatResponse(
            updatedConcertSeat.concertSeatId!!,
            updatedConcertSeat.seatNumber,
            updatedConcertSeat.price,
            updatedConcertSeat.reservationStatus.toString()
        )
    }

    // 예약된 좌석 결제
    @Transactional
    fun payForTemporaryReservedSeatToConfirmedReservation(token: String, concertSeatId: Long) {
        val tokenQueue = tokenQueueService.getByToken(token)
        val concertSeat = concertSeatService.payForTemporaryReservedSeatToConfirmedReserved(concertSeatId = concertSeatId, userId = tokenQueue.userId)
        concertReservationHistoryService.create(
            concertSeatId = concertSeat.concertSeatId!!,
            status = concertSeat.reservationStatus
        )

        val wallet = walletService.useBalance(userId = tokenQueue.userId, amount = concertSeat.price)

        concertSeatPaymentHistoryService.create(
            walletId = wallet.walletId!!,
            concertSeatId = concertSeat.concertSeatId!!,
            price = concertSeat.price
        )
        walletHistoryService.create(
            walletId = wallet.walletId!!,
            balance = wallet.balance,
            amount = -concertSeat.price
        )
    }
}