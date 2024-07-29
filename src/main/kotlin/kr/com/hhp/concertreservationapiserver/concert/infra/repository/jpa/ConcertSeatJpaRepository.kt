package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import jakarta.persistence.LockModeType
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface ConcertSeatJpaRepository: JpaRepository<ConcertSeatEntity, Long> {

    fun findAllByConcertDetailIdAndReservationStatus(
        concertDetailId: Long, reservationStatus: ConcertReservationStatus
    ): List<ConcertSeatEntity>

    fun findByConcertSeatId(concertSeatId: Long): ConcertSeatEntity?

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT concertSeat FROM concert_seat as concertSeat WHERE concertSeat.concertSeatId = :concertSeatId")
    fun findByConcertSeatIdWithSLock(concertSeatId: Long): ConcertSeatEntity?

    fun findAllByReservationStatusAndUpdatedAtIsAfter(status: ConcertReservationStatus, date: LocalDateTime): List<ConcertSeatEntity>
}