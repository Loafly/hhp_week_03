package kr.com.hhp.concertreservationapiserver.concert.business.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity

interface ConcertReservationHistoryRepository {

    fun save(concertReservationHistory: ConcertReservationHistoryEntity): ConcertReservationHistoryEntity
    fun saveAll(concertReservationHistories: List<ConcertReservationHistoryEntity>): List<ConcertReservationHistoryEntity>
}