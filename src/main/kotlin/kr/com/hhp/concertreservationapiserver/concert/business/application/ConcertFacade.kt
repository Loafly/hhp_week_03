package kr.com.hhp.concertreservationapiserver.concert.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertBaseService
import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletHistoryService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Facade
class ConcertFacade(
    private val tokenQueueService: TokenQueueService,
    private val walletService: WalletService,
    private val walletHistoryService: WalletHistoryService,
    private val concertBaseService: ConcertBaseService
) {

    // 예약 가능 날짜 조회
    @Transactional(readOnly = true)
    fun getAllAvailableReservationDetail(concertId: Long, reservationDateTime: LocalDateTime): List<ConcertDto.Detail> {

        return concertBaseService.getAllAvailableReservationDetail(
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

        return concertBaseService.getAllAvailableReservationSeat(concertDetailId)
            .map { ConcertDto.Seat(
                concertSeatId = it.concertSeatId!!,
                seatNumber = it.seatNumber,
                price = it.price,
                reservationStatus = it.reservationStatus.toString()
            ) }
    }

    // 좌석 임시 예약
    @Transactional
    fun reserveSeatToTemporary(token: String, concertSeatId: Long): ConcertDto.Seat {
        val tokenQueue = tokenQueueService.getByToken(token)
        val concertSeat = concertBaseService.reserveSeatToTemporary(concertSeatId, tokenQueue.userId)

        return ConcertDto.Seat(
            concertSeat.concertSeatId!!,
            concertSeat.seatNumber,
            concertSeat.price,
            concertSeat.reservationStatus.toString()
        )
    }

    // 예약된 좌석 결제
    @Transactional
    fun payForTemporaryReservedSeatToConfirmedReservation(token: String, concertSeatId: Long) {
        val tokenQueue = tokenQueueService.getByToken(token)
        val wallet = walletService.getByUserId(userId = tokenQueue.userId)
        val concertSeat = concertBaseService.payForTemporaryReservedSeatToConfirmedReservation(
            concertSeatId, tokenQueue.userId, wallet.walletId!!
        )

        walletHistoryService.create(
            walletId = wallet.walletId!!,
            balance = wallet.balance,
            amount = -concertSeat.price
        )
    }
}