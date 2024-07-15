package kr.com.hhp.concertreservationapiserver.concert.domain

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatPaymentHistoryEntity

interface ConcertSeatPaymentHistoryRepository {

    fun save(concertSeatPaymentHistoryEntity: ConcertSeatPaymentHistoryEntity): ConcertSeatPaymentHistoryEntity
}