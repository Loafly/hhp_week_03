package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import jakarta.persistence.LockModeType
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ConcertDetailJpaRepository: JpaRepository<ConcertDetailEntity, Long> {


    fun findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
        concertId: Long,
        remainingSeatCount: Int, // 일치하지 않는 경우
        reservationStartDateTime: LocalDateTime,
        reservationEndDateTime: LocalDateTime
    ): List<ConcertDetailEntity>

    fun findByConcertDetailId(concertDetailId: Long): ConcertDetailEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cd FROM concert_detail as cd WHERE cd.concertDetailId in (:concertDetailIds)")
    fun findAllByConcertDetailIdInWithXLock(concertDetailIds: List<Long>): List<ConcertDetailEntity>
}