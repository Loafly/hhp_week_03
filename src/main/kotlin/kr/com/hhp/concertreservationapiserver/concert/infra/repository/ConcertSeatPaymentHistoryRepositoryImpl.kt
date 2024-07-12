package kr.com.hhp.concertreservationapiserver.concert.infra.repository

import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertSeatPaymentHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatPaymentHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertSeatPaymentHistoryJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ConcertSeatPaymentHistoryRepositoryImpl(
    private val concertSeatPaymentHistoryJpaRepository: ConcertSeatPaymentHistoryJpaRepository
): ConcertSeatPaymentHistoryRepository {

    override fun save(concertSeatPaymentHistoryEntity: ConcertSeatPaymentHistoryEntity): ConcertSeatPaymentHistoryEntity {
        return concertSeatPaymentHistoryJpaRepository.save(concertSeatPaymentHistoryEntity)
    }
}