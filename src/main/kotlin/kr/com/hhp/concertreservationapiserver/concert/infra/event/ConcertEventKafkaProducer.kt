package kr.com.hhp.concertreservationapiserver.concert.infra.event

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.service.OutBoxService
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEventPublisher
import org.springframework.context.annotation.Primary
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Primary
@Component
class ConcertEventKafkaProducer(private val kafkaTemplate: KafkaTemplate<String, Any>,
                                private val outBoxService: OutBoxService): ConcertEventPublisher {

    override fun publishReservationEvent(concertSeatId: Long) {
        val topic = "concertReservation"
        val reservation = ConcertEvent.Reservation(concertSeatId)
        val outBoxEntity = outBoxService.create(payload = Json.encodeToString(reservation), eventType = topic)

        runCatching {
            kafkaTemplate.send(topic, reservation)
        }.onSuccess {
            outBoxEntity.updateStatus(EventStatus.PUBLISHED)
        }.onFailure {
            outBoxEntity.updateStatus(EventStatus.FAIL)
        }

        outBoxService.save(outBoxEntity)
    }

    override fun publishPaymentEvent(concertSeatId: Long, userId: Long, price: Int, walletId: Long, token: String) {
        val topic = "concertPay"
        val pay = ConcertEvent.Pay(
            walletId = walletId, userId = userId, price = price, concertSeatId = concertSeatId, token = token
        )
        val outBoxEntity = outBoxService.create(payload = pay.toString(), eventType = topic)

        runCatching {
            kafkaTemplate.send("concertPay", pay)
        }.onSuccess {
            outBoxEntity.updateStatus(EventStatus.PUBLISHED)
        }.onFailure {
            outBoxEntity.updateStatus(EventStatus.FAIL)
        }

    }
}