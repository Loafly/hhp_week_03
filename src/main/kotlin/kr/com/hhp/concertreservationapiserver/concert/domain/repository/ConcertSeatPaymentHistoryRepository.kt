package kr.com.hhp.concertreservationapiserver.concert.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatPaymentHistoryEntity

interface ConcertSeatPaymentHistoryRepository {

    fun save(concertSeatPaymentHistoryEntity: ConcertSeatPaymentHistoryEntity): ConcertSeatPaymentHistoryEntity
}