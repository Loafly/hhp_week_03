package kr.com.hhp.concertreservationapiserver.concert.infra.repository

import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertSeatJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ConcertSeatRepositoryImpl(private val concertSeatJpaRepository: ConcertSeatJpaRepository): ConcertSeatRepository {
    override fun findAllByConcertDetailIdAndReservationStatus(
        concertDetailId: Long,
        reservationStatus: ConcertReservationStatus
    ): List<ConcertSeatEntity> {
        return concertSeatJpaRepository.findAllByConcertDetailIdAndReservationStatus(
            concertDetailId = concertDetailId,
            reservationStatus = reservationStatus
        )
    }

    override fun findByConcertSeatId(concertSeatId: Long): ConcertSeatEntity? {
        return concertSeatJpaRepository.findByConcertSeatId(concertSeatId)
    }

    override fun findByConcertSeatIdWithXLock(concertSeatId: Long): ConcertSeatEntity? {
        return concertSeatJpaRepository.findByConcertSeatIdWithXLock(concertSeatId)
    }

    override fun save(concertSeatEntity: ConcertSeatEntity): ConcertSeatEntity {
        return concertSeatJpaRepository.save(concertSeatEntity)
    }

    override fun saveAll(concertSeats: List<ConcertSeatEntity>): List<ConcertSeatEntity> {
        return concertSeatJpaRepository.saveAll(concertSeats)
    }

    override fun findAllByReservationStatusAndUpdatedAtIsAfter(
        status: ConcertReservationStatus,
        date: LocalDateTime
    ): List<ConcertSeatEntity> {
        return concertSeatJpaRepository.findAllByReservationStatusAndUpdatedAtIsAfter(
            status = status,
            date = date
        )
    }
}