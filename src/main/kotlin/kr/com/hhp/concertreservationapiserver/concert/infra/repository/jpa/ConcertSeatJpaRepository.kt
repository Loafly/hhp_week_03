package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.time.LocalDateTime

interface ConcertSeatJpaRepository: JpaRepository<ConcertSeatEntity, Long> {

    fun findAllByConcertDetailIdAndReservationStatus(
        concertDetailId: Long, reservationStatus: ConcertReservationStatus
    ): List<ConcertSeatEntity>

    fun findByConcertSeatId(concertSeatId: Long): ConcertSeatEntity?

    fun findAllByReservationStatusAndUpdatedAtIsAfter(status: ConcertReservationStatus, date: LocalDateTime): List<ConcertSeatEntity>
}