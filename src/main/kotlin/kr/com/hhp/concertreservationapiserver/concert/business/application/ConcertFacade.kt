package kr.com.hhp.concertreservationapiserver.concert.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.DistributedSimpleLock
import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEventPublisher
import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertService
import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletBalanceType
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Facade
class ConcertFacade(
    private val tokenQueueService: TokenQueueService,
    private val walletService: WalletService,
    private val concertService: ConcertService,
    private val concertEventPublisher: ConcertEventPublisher,
) {

    // 예약 가능 날짜 조회
    @Transactional(readOnly = true)
    fun getAllAvailableReservationDetail(concertId: Long, reservationDateTime: LocalDateTime): List<ConcertDto.Detail> {

        return concertService.getAllAvailableReservationDetail(
            concertId = concertId, reservationDateTime = reservationDateTime
        ).map {
            ConcertDto.Detail(
                concertId = it.concertId,
                totalSeatCount = it.totalSeatCount,
                remainingSeatCount = it.remainingSeatCount,
                reservationStartDateTime = it.reservationStartDateTime,
                reservationEndDateTime = it.reservationEndDateTime
            )
        }
    }

    // 예약 가능 좌석 조회
    @Transactional(readOnly = true)
    fun getAllAvailableReservationSeat(concertDetailId: Long): List<ConcertDto.Seat> {

        return concertService.getAllAvailableReservationSeat(concertDetailId)
            .map {
                ConcertDto.Seat(
                    concertSeatId = it.concertSeatId!!,
                    seatNumber = it.seatNumber,
                    price = it.price,
                    reservationStatus = it.reservationStatus.toString()
                )
            }
    }

    // 좌석 임시 예약
    @DistributedSimpleLock(key = "'concertSeatId:' + #concertSeatId")
    fun reserveSeatToTemporary(token: String, concertSeatId: Long): ConcertDto.Seat {
        val userId = tokenQueueService.getUserIdByToken(token)
        val concertSeat = concertService.reserveSeatToTemporary(concertSeatId, userId)

        concertEventPublisher.publishReservationEvent(concertSeatId = concertSeat.concertSeatId!!)

        return ConcertDto.Seat(
            concertSeat.concertSeatId!!,
            concertSeat.seatNumber,
            concertSeat.price,
            concertSeat.reservationStatus.toString()
        )
    }

    // 좌석 임시 예약 이후 처리
    @Transactional
    fun reserveSeat(concertSeatId: Long) {
        concertService.createReserveHistory(concertSeatId = concertSeatId, reservationStatus = ConcertReservationStatus.T)
    }

    // 예약된 좌석 결제
    @Transactional
    fun payForTemporaryReservedSeatToConfirmedReservation(token: String, concertSeatId: Long) {
        val userId = tokenQueueService.getUserIdByToken(token)
        val wallet = walletService.getByUserId(userId = userId)
        val concertSeat = concertService.payForTemporaryReservedSeatToConfirmedReservation(
            concertSeatId = concertSeatId, userId = userId, walletId = wallet.walletId!!
        )

        walletService.updateBalance(
            walletId = wallet.walletId!!,
            userId = userId,
            amount = concertSeat.price,
            balanceType = WalletBalanceType.U
        )

        concertEventPublisher.publishPaymentEvent(
            concertSeatId = concertSeat.concertSeatId!!,
            token = token,
            walletId = wallet.walletId!!,
            userId = userId,
            price = concertSeat.price
        )
    }

    // 결제 이후 처리
    @Transactional
    fun paymentSeat(concertSeatId: Long, walletId: Long, userId: Long, price: Int, token: String) {
        concertService.createReserveHistory(concertSeatId = concertSeatId, reservationStatus = ConcertReservationStatus.C)
        concertService.createSeatPaymentHistory(concertSeatId = concertSeatId, price = price, walletId = walletId)

        //토큰 제거
        tokenQueueService.deleteActiveToken(token)
    }

}