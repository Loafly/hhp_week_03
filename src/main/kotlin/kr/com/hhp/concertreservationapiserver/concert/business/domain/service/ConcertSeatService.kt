package kr.com.hhp.concertreservationapiserver.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ConcertSeatService(private val concertSeatRepository : ConcertSeatRepository) {

    fun getAllReservationStatusIsAvailableByConcertDetailId(concertDetailId: Long): List<ConcertSeatEntity> {
        return concertSeatRepository.findAllByConcertDetailIdAndReservationStatus(
            concertDetailId = concertDetailId,
            reservationStatus = ConcertReservationStatus.A
        )
    }

    fun getByConcertSeatId(concertSeatId: Long): ConcertSeatEntity {
        return concertSeatRepository.findByConcertSeatId(concertSeatId)
            ?: throw CustomException(ErrorCode.CONCERT_SEAT_NOT_FOUND)
    }

    fun getByConcertSeatIdWithXLock(concertSeatId: Long): ConcertSeatEntity {
        return concertSeatRepository.findByConcertSeatIdWithXLock(concertSeatId)
            ?: throw CustomException(ErrorCode.CONCERT_SEAT_NOT_FOUND)
    }

    fun releaseExpiredReservations(): List<ConcertSeatEntity> {

        val expiredConcertSeats = getAllExpiredReservationTemporaries()

        expiredConcertSeats.forEach{ it.expiredTemporaryReservation() }

        return concertSeatRepository.saveAll(expiredConcertSeats)
    }

    fun getAllExpiredReservationTemporaries(): List<ConcertSeatEntity> {
        return concertSeatRepository.findAllByReservationStatusAndUpdatedAtIsAfter(
            ConcertReservationStatus.T,
            LocalDateTime.now().minusMinutes(30)
        )
    }
}