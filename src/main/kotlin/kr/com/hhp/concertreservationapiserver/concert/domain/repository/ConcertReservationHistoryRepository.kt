package kr.com.hhp.concertreservationapiserver.concert.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationHistoryEntity

interface ConcertReservationHistoryRepository {

    fun save(concertReservationHistory: ConcertReservationHistoryEntity): ConcertReservationHistoryEntity
    fun saveAll(concertReservationHistories: List<ConcertReservationHistoryEntity>): List<ConcertReservationHistoryEntity>
}