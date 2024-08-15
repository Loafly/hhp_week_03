package kr.com.hhp.concertreservationapiserver.integration

import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import kr.com.hhp.concertreservationapiserver.concert.infra.event.ConcertEventKafkaProducer
import kr.com.hhp.concertreservationapiserver.concert.presentation.event.ConcertReservationConsumer
import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@ExtendWith(SpringExtension::class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class ConcertKafkaIntegrationTest {

    @Autowired
    lateinit var concertEventKafkaProducer: ConcertEventKafkaProducer

    @SpyBean
    lateinit var concertReservationConsumer: ConcertReservationConsumer

    @MockBean
    lateinit var tokenQueueService: TokenQueueService

    @Test
    fun `콘서트 좌석 임시 예약 카프카 테스트`() {
        // given
        val concertSeatId = 1L
        val latch = CountDownLatch(1)

        val reservation = ConcertEvent.Reservation(concertSeatId)

        given(concertReservationConsumer.listen(reservation)).willAnswer { latch.countDown() }

        // when
        concertEventKafkaProducer.publishReservationEvent(concertSeatId)

        // then
        val messageConsumed = latch.await(5, TimeUnit.SECONDS)
        assertTrue(messageConsumed, "메시지가 소비되지 않았습니다.")
        then(concertReservationConsumer).should().listen(reservation)
    }


    @Test
    fun `콘서트 좌석 결제 카프카 테스트`() {
        // given
        val concertSeatId = 1L
        val walletId = 1L
        val userId = 1L
        val price = 50000
        val token = UUID.randomUUID().toString()

        val latch = CountDownLatch(1)

        val pay = ConcertEvent.Pay(
            concertSeatId = concertSeatId, walletId = walletId, userId = userId, price = price, token = token
        )

        given(concertReservationConsumer.listen(pay)).willAnswer { latch.countDown() }
        doNothing().whenever(tokenQueueService).deleteActiveToken(token)

        // when
        concertEventKafkaProducer.publishPaymentEvent(
            concertSeatId = concertSeatId, userId = userId, price = price, walletId = walletId, token = token
        )

        // then
        val messageConsumed = latch.await(5, TimeUnit.SECONDS)
        assertTrue(messageConsumed, "메시지가 소비되지 않았습니다.")
        then(concertReservationConsumer).should().listen(pay)
    }
}