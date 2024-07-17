package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertSeatPaymentHistoryService
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertSeatPaymentHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatPaymentHistoryEntity
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.then

@ExtendWith(MockitoExtension::class)
class ConcertSeatPaymentHistoryServiceTest {

    @Mock
    private lateinit var concertSeatPaymentHistoryRepository: ConcertSeatPaymentHistoryRepository

    @InjectMocks
    private lateinit var concertSeatPaymentHistoryService: ConcertSeatPaymentHistoryService

    @Nested
    @DisplayName("콘서트 좌석 결제 내역 생성")
    inner class CreateTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val walletId = 1L
            val concertSeatId = 1L
            val price = 1000

            val expectedConcertSeatPaymentHistory = ConcertSeatPaymentHistoryEntity(
                concertSeatId = concertSeatId,
                price = price,
                walletId = walletId
            )
            given(concertSeatPaymentHistoryRepository.save(any())).willReturn(expectedConcertSeatPaymentHistory)

            //when
            val concertSeatPaymentHistory = concertSeatPaymentHistoryService.create(
                walletId = walletId,
                concertSeatId = concertSeatId,
                price = price
            )

            //then
            then(concertSeatPaymentHistoryRepository).should().save(any())
            assertEquals(expectedConcertSeatPaymentHistory.concertSeatId, concertSeatPaymentHistory.concertSeatId)
            assertEquals(expectedConcertSeatPaymentHistory.walletId, concertSeatPaymentHistory.walletId)
            assertEquals(expectedConcertSeatPaymentHistory.price, concertSeatPaymentHistory.price)
        }
    }
}