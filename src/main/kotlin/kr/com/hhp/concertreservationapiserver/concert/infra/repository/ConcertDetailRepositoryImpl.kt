package kr.com.hhp.concertreservationapiserver.concert.infra.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertDetailJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ConcertDetailRepositoryImpl(private val concertDetailJpaRepository: ConcertDetailJpaRepository):
    ConcertDetailRepository {
    override fun save(concertDetailEntity: ConcertDetailEntity): ConcertDetailEntity {
        return concertDetailJpaRepository.save(concertDetailEntity)
    }

    override fun findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
        concertId: Long,
        remainingSeatCount: Int,
        reservationStartDateTime: LocalDateTime,
        reservationEndDateTime: LocalDateTime
    ): List<ConcertDetailEntity> {
        return concertDetailJpaRepository.findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
            concertId = concertId,
            remainingSeatCount = remainingSeatCount,
            reservationStartDateTime = reservationStartDateTime,
            reservationEndDateTime = reservationEndDateTime
        )
    }

    override fun findByConcertDetailId(concertDetailId: Long): ConcertDetailEntity? {
        return concertDetailJpaRepository.findByConcertDetailId(concertDetailId)
    }
}