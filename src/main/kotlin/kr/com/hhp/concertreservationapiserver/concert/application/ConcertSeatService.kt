package kr.com.hhp.concertreservationapiserver.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatAlreadyReservedException
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatIsNotTemporaryStatusException
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserIdMisMatchException
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
            ?: throw ConcertSeatNotFoundException("ConcertSeat이 존재하지 않습니다. concertSeatId : $concertSeatId")
    }

    fun throwExceptionIfStatusIsNotTemporary(concertSeatEntity: ConcertSeatEntity){
        if(concertSeatEntity.reservationStatus != ConcertReservationStatus.T) {
            throw ConcertSeatIsNotTemporaryStatusException("임시 예약된 좌석이 아닙니다.")
        }
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

    fun throwExceptionIfStatusIsNotAvailable(concertSeat: ConcertSeatEntity){
        if(concertSeat.reservationStatus != ConcertReservationStatus.A) {
            throw ConcertSeatAlreadyReservedException("이미 예약된 좌석입니다.")
        }
    }

    fun throwExceptionIfMisMatchUserId(concertSeat: ConcertSeatEntity, userId: Long){
        if(concertSeat.userId != userId) {
            throw UserIdMisMatchException("유저Id가 일치하지 않습니다. userId : ${userId}, concertSeatId.userId : ${concertSeat.userId}")
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