package kr.com.hhp.concertreservationapiserver.concert.business.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import java.time.LocalDateTime

interface ConcertDetailRepository {

    fun save(concertDetailEntity: ConcertDetailEntity): ConcertDetailEntity
    fun saveAll(concertDetailEntityList: List<ConcertDetailEntity>): List<ConcertDetailEntity>

    fun findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
        concertId: Long,
        remainingSeatCount: Int, // 일치하지 않는 경우
        reservationStartDateTime: LocalDateTime,
        reservationEndDateTime: LocalDateTime
    ): List<ConcertDetailEntity>

    fun findByConcertDetailId(concertDetailId: Long): ConcertDetailEntity?

    fun findAllByConcertDetailIdInWithXLock(concertDetailIds: List<Long>): List<ConcertDetailEntity>
}