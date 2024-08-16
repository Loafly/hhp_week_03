package kr.com.hhp.concertreservationapiserver.common.presentation.scheduler

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.repository.OutBoxRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutBoxScheduler(
    private val outBoxRepository: OutBoxRepository,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {

    // 5분마다 아웃박스 실패된 케이스 재발행
    @OptIn(InternalSerializationApi::class)
    @Transactional
    @Scheduled(cron = "0 0/7 * * * *")
    fun republishToFailEvent(){
        val allByFailStatus = outBoxRepository.findAllByEventStatus(EventStatus.FAIL)

        allByFailStatus.forEach { outBox ->
            runCatching {
                val payloadClass = when (outBox.eventType) {
                    "concertReservation" -> ConcertEvent.Reservation::class
                    "concertPay" -> ConcertEvent.Pay::class
                    else -> throw IllegalArgumentException("Unknown event type: ${outBox.eventType}")
                }
                kafkaTemplate.send(outBox.eventType, Json.decodeFromString(payloadClass.serializer(), outBox.payload))
            }.onSuccess {
                outBox.updateStatus(EventStatus.PUBLISHED)
            }.onFailure {
                outBox.updateStatus(EventStatus.FAIL)
            }
        }

        outBoxRepository.saveAll(allByFailStatus)
    }
}