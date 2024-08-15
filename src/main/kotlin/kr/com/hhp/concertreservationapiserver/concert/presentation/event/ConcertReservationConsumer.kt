package kr.com.hhp.concertreservationapiserver.concert.presentation.event

import kr.com.hhp.concertreservationapiserver.concert.business.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class ConcertReservationConsumer(private val concertFacade: ConcertFacade) {

    @KafkaListener(topics = ["concertReservation"])
    fun listen(event: ConcertEvent.Reservation) {
        // 콘서트 임시 예약 이후 처리
        concertFacade.reserveSeat(concertSeatId = event.concertSeatId)
    }

    @KafkaListener(topics = ["concertPay"])
    fun listen(event: ConcertEvent.Pay) {
        // 콘서트 임시 예약 이후 처리
        concertFacade.paymentSeat(
            concertSeatId = event.concertSeatId,
            walletId = event.walletId,
            userId = event.userId,
            price = event.price,
            token = event.token
        )
    }
}