package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertReservationHistoryService
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
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
class ConcertDtoReservationHistoryServiceTest {

    @Mock
    private lateinit var concertReservationHistoryRepository: ConcertReservationHistoryRepository

    @InjectMocks
    private lateinit var concertReservationHistoryService: ConcertReservationHistoryService

    @Nested
    @DisplayName("콘서트 예약 내역 생성")
    inner class CreateTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertSeatId = 1L
            val status = ConcertReservationStatus.T
            val expectedConcertReservationHistory = ConcertReservationHistoryEntity(
                concertSeatId = concertSeatId,
                status = status
            )
            given(concertReservationHistoryRepository.save(any())).willReturn(expectedConcertReservationHistory)

            //when
            val concertReservationHistoryEntity = concertReservationHistoryService.create(concertSeatId = concertSeatId, status = status)

            //then
            then(concertReservationHistoryRepository).should().save(any())
            assertEquals(expectedConcertReservationHistory.status, concertReservationHistoryEntity.status)
            assertEquals(expectedConcertReservationHistory.concertSeatId, concertReservationHistoryEntity.concertSeatId)
        }
    }

    @Nested
    @DisplayName("콘서트 예약 내역 단체 생성")
    inner class CreateAllTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertSeatIds = listOf(1L, 2L)
            val status = ConcertReservationStatus.T
            val expectedConcertReservationHistories = listOf(
                ConcertReservationHistoryEntity(
                    concertSeatId = concertSeatIds[0],
                    status = ConcertReservationStatus.T
                ),
                ConcertReservationHistoryEntity(
                    concertSeatId = concertSeatIds[1],
                    status = ConcertReservationStatus.A
                ),
            )
            given(concertReservationHistoryRepository.saveAll(any())).willReturn(expectedConcertReservationHistories)

            //when
            val concertReservationHistoryEntities = concertReservationHistoryService.createAll(concertSeatIds = concertSeatIds, status = status)

            //then
            then(concertReservationHistoryRepository).should().saveAll(any())
            assertEquals(expectedConcertReservationHistories[0].status, concertReservationHistoryEntities[0].status)
            assertEquals(expectedConcertReservationHistories[0].concertSeatId, concertReservationHistoryEntities[0].concertSeatId)
            assertEquals(expectedConcertReservationHistories[1].status, concertReservationHistoryEntities[1].status)
            assertEquals(expectedConcertReservationHistories[1].concertSeatId, concertReservationHistoryEntities[1].concertSeatId)
        }
    }
}