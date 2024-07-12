package kr.com.hhp.concertreservationapiserver.concert.domain

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertDetailEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface ConcertDetailRepository {

    fun save(concertDetailEntity: ConcertDetailEntity): ConcertDetailEntity

    fun findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
        concertId: Long,
        remainingSeatCount: Int, // 일치하지 않는 경우
        reservationStartDateTime: LocalDateTime,
        reservationEndDateTime: LocalDateTime
    ): List<ConcertDetailEntity>

    fun findByConcertDetailId(concertDetailId: Long): ConcertDetailEntity?
}