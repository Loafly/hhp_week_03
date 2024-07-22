package kr.com.hhp.concertreservationapiserver.concert.business.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatPaymentHistoryEntity

interface ConcertSeatPaymentHistoryRepository {

    fun save(concertSeatPaymentHistoryEntity: ConcertSeatPaymentHistoryEntity): ConcertSeatPaymentHistoryEntity
}