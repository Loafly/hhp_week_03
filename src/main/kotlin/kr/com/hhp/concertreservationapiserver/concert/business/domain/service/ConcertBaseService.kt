package kr.com.hhp.concertreservationapiserver.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ConcertBaseService(
    private val concertService: ConcertService,
    private val concertDetailService: ConcertDetailService,
    private val concertSeatService: ConcertSeatService,
    private val concertReservationHistoryService: ConcertReservationHistoryService,
    private val concertSeatPaymentHistoryService: ConcertSeatPaymentHistoryService
) {

    // 예약 가능 날짜 조회
    fun getAllAvailableReservationDetail(concertId: Long, reservationDateTime: LocalDateTime): List<ConcertDetailEntity> {
        // 실제 존재하는 concertId가 맞는지 확인
        val concert = concertService.getByConcertId(concertId)
        val availableReservationConcertDetails = concertDetailService.getAllAvailableReservationByConcertId(
            concertId = concert.concertId!!,
            reservationDateTime = reservationDateTime
        )

        return availableReservationConcertDetails
    }

    // 예약 가능 좌석 조회
    fun getAllAvailableReservationSeat(concertDetailId: Long): List<ConcertSeatEntity> {
        val concertDetail = concertDetailService.getByConcertDetailId(concertDetailId)

        return concertSeatService.getAllReservationStatusIsAvailableByConcertDetailId(concertDetail.concertDetailId!!)
    }


    // 좌석 임시 예약
    fun reserveSeatToTemporary(concertSeatId: Long, userId: Long) :ConcertSeatEntity {
        val concertSeat = concertSeatService.getByConcertSeatIdWithXLock(concertSeatId)
        val concertDetail = concertDetailService.getByConcertDetailId(concertSeat.concertDetailId)
        concertDetail.throwExceptionIfNotReservationPeriod();

        concertSeat.updateReservationStatusT(userId)

        concertReservationHistoryService.create(
            concertSeatId = concertSeat.concertSeatId!!,
            status = concertSeat.reservationStatus
        )

        return concertSeat
    }

    //예약된 좌석 결제
    fun payForTemporaryReservedSeatToConfirmedReservation(concertSeatId: Long, userId: Long, walletId: Long): ConcertSeatEntity {
        val concertSeat = concertSeatService.getByConcertSeatIdWithXLock(concertSeatId)
        concertSeat.updateReservationStatusC(userId)
        concertReservationHistoryService.create(
            concertSeatId = concertSeat.concertSeatId!!,
            status = concertSeat.reservationStatus
        )

        concertSeatPaymentHistoryService.create(
            walletId = walletId,
            concertSeatId = concertSeat.concertSeatId!!,
            price = concertSeat.price
        )

        return concertSeat
    }
}