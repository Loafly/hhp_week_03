package kr.com.hhp.concertreservationapiserver.concert.domain.service

import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertSeatPaymentHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatPaymentHistoryEntity
import org.springframework.stereotype.Service

@Service
class ConcertSeatPaymentHistoryService(private val concertSeatPaymentHistoryRepository: ConcertSeatPaymentHistoryRepository) {

    fun create(walletId: Long, concertSeatId: Long, price: Int): ConcertSeatPaymentHistoryEntity {

        return concertSeatPaymentHistoryRepository.save(
            ConcertSeatPaymentHistoryEntity(
                concertSeatId = concertSeatId,
                price = price,
                walletId = walletId
            )
        )
    }

}