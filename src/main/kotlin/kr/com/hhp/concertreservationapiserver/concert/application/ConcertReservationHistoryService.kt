package kr.com.hhp.concertreservationapiserver.concert.application

import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
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