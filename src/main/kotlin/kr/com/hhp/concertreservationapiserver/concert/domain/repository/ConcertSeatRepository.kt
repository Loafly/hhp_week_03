package kr.com.hhp.concertreservationapiserver.concert.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import java.time.LocalDateTime

interface ConcertSeatRepository {
    fun findAllByConcertDetailIdAndReservationStatus(
        concertDetailId: Long, reservationStatus: ConcertReservationStatus
    ): List<ConcertSeatEntity>

    fun findByConcertSeatId(concertSeatId: Long): ConcertSeatEntity?
    fun findByConcertSeatIdWithXLock(concertSeatId: Long): ConcertSeatEntity?

    fun save(concertSeatEntity: ConcertSeatEntity): ConcertSeatEntity
    fun saveAll(concertSeats: List<ConcertSeatEntity>): List<ConcertSeatEntity>

    fun findAllByReservationStatusAndUpdatedAtIsAfter(status: ConcertReservationStatus, date: LocalDateTime): List<ConcertSeatEntity>
}