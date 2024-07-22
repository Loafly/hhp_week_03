package kr.com.hhp.concertreservationapiserver.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import org.springframework.stereotype.Service

@Service
class ConcertReservationHistoryService(private val concertReservationHistoryRepository: ConcertReservationHistoryRepository) {

    fun create(concertSeatId: Long, status: ConcertReservationStatus): ConcertReservationHistoryEntity {
        return concertReservationHistoryRepository.save(
            ConcertReservationHistoryEntity(
            concertSeatId = concertSeatId,
            status = status
            )
        )
    }

    fun createAll(concertSeatIds: List<Long>, status: ConcertReservationStatus): List<ConcertReservationHistoryEntity> {
        val concertReservationHistories = mutableListOf<ConcertReservationHistoryEntity>()

        concertSeatIds.forEach{
            concertReservationHistories.add(
                ConcertReservationHistoryEntity(
                    concertSeatId = it,
                    status = status
                )
            )
        }

        return concertReservationHistoryRepository.saveAll(concertReservationHistories)
    }
}