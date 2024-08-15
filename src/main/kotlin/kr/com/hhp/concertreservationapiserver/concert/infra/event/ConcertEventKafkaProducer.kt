package kr.com.hhp.concertreservationapiserver.concert.infra.event

import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEventPublisher
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Primary
@Component
class ConcertEventKafkaProducer(private val kafkaTemplate: KafkaTemplate<String, Any>): ConcertEventPublisher {

    override fun publishReservationEvent(concertSeatId: Long) {
        kafkaTemplate.send("concertReservation", ConcertEvent.Reservation(concertSeatId))
    }

    override fun publishPaymentEvent(concertSeatId: Long, userId: Long, price: Int, walletId: Long, token: String) {
        kafkaTemplate.send("concertPay", ConcertEvent.Pay(
            walletId = walletId,
            userId = userId,
            price = price,
            concertSeatId = concertSeatId,
            token = token
        ))
    }
}