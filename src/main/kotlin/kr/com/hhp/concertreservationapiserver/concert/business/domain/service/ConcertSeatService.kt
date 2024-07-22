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

    fun payForTemporaryReservedSeatToConfirmedReserved(concertSeatId: Long, userId: Long): ConcertSeatEntity {
        val concertSeat = getByConcertSeatId(concertSeatId)
        throwExceptionIfStatusIsNotTemporary(concertSeat)
        throwExceptionIfMisMatchUserId(concertSeat = concertSeat, userId = userId)
        concertSeat.updateReservationStatusC()
        return concertSeatRepository.save(concertSeat)
    }

    fun reserveSeatToTemporary(concertSeatId: Long, userId: Long): ConcertSeatEntity {
        val concertSeat = getByConcertSeatId(concertSeatId)
        throwExceptionIfStatusIsNotAvailable(concertSeat)

        concertSeat.updateReservationStatusT(userId)
        return concertSeatRepository.save(concertSeat)
    }

    private fun throwExceptionIfStatusIsNotTemporary(concertSeatEntity: ConcertSeatEntity){
        if(concertSeatEntity.reservationStatus != ConcertReservationStatus.T) {
            throw CustomException(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS)
        }
    }

    private fun throwExceptionIfStatusIsNotAvailable(concertSeat: ConcertSeatEntity){
        if(concertSeat.reservationStatus != ConcertReservationStatus.A) {
            throw CustomException(ErrorCode.CONCERT_SEAT_ALREADY_RESERVED)
        }
    }

    private fun throwExceptionIfMisMatchUserId(concertSeat: ConcertSeatEntity, userId: Long){
        if(concertSeat.userId != userId) {
            throw CustomException(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH)
        }
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