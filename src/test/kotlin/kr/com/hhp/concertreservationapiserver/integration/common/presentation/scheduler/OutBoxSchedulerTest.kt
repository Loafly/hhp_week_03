package kr.com.hhp.concertreservationapiserver.integration.common.presentation.scheduler

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity
import kr.com.hhp.concertreservationapiserver.common.domain.repository.OutBoxRepository
import kr.com.hhp.concertreservationapiserver.common.presentation.scheduler.OutBoxScheduler
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import kr.com.hhp.concertreservationapiserver.concert.presentation.event.ConcertReservationConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Transactional
@ExtendWith(SpringExtension::class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class OutBoxSchedulerTest {

    @Autowired
    lateinit var outBoxScheduler: OutBoxScheduler

    @SpyBean
    lateinit var concertReservationConsumer: ConcertReservationConsumer

    @Autowired
    lateinit var outBoxRepository: OutBoxRepository

    @Test
    fun `실패 케이스 카프카 재발송`() {
        // given
        val concertSeatId = 1L
        val latch = CountDownLatch(1)
        val topic = "concertReservation"
        val reservation = ConcertEvent.Reservation(concertSeatId)
        val outBoxEntity = outBoxRepository.save(
            OutBoxEntity(payload = Json.encodeToString(reservation), eventType = topic, eventStatus = EventStatus.FAIL)
        )
        outBoxRepository.save(outBoxEntity)
        given(concertReservationConsumer.listen(reservation)).willAnswer { latch.countDown() }

        // when
        outBoxScheduler.republishToFailEvent()

        // then
        val messageConsumed = latch.await(5, TimeUnit.SECONDS)
        kotlin.test.assertTrue(messageConsumed, "메시지가 소비되지 않았습니다.")
        then(concertReservationConsumer).should().listen(reservation)
    }
}