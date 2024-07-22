package kr.com.hhp.concertreservationapiserver.concert.infra.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertReservationHistoryJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ConcertReservationHistoryRepositoryImpl(private val concertReservationHistoryJpaRepository: ConcertReservationHistoryJpaRepository):
    ConcertReservationHistoryRepository {

    override fun save(concertReservationHistory: ConcertReservationHistoryEntity): ConcertReservationHistoryEntity {
        return concertReservationHistoryJpaRepository.save((concertReservationHistory))
    }

    override fun saveAll(concertReservationHistories: List<ConcertReservationHistoryEntity>): List<ConcertReservationHistoryEntity> {
        return concertReservationHistoryJpaRepository.saveAll(concertReservationHistories)
    }

}