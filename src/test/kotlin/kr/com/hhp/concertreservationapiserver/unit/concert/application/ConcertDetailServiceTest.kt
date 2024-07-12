package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.ConcertDetailService
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertDetailNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertDetailEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ConcertDetailServiceTest {

    @Mock
    private lateinit var concertDetailRepository: ConcertDetailRepository

    @InjectMocks
    private lateinit var concertDetailService: ConcertDetailService

    @Nested
    @DisplayName("특정 콘서트의 예약 가능한 날짜 조회")
    inner class GetAllAvailableReservationByConcertIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertId = 1L
            val reservationDateTime = LocalDateTime.now()

            val expectedConcertDetails =  listOf(
                ConcertDetailEntity(
                    concertId = concertId,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                ),
                ConcertDetailEntity(
                    concertId = concertId,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                ),
                ConcertDetailEntity(
                    concertId = concertId,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                ),
            )


            given(concertDetailRepository.findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
                    concertId = concertId,
                    remainingSeatCount = 0,
                    reservationStartDateTime = reservationDateTime,
                    reservationEndDateTime = reservationDateTime
                )
            ).willReturn(expectedConcertDetails)

            //when
            val concertDetails = concertDetailService.getAllAvailableReservationByConcertId(
                concertId = concertId,
                reservationDateTime = reservationDateTime
            )

            //then
            then(concertDetailRepository).should().findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
                concertId = concertId,
                remainingSeatCount = 0,
                reservationStartDateTime = reservationDateTime,
                reservationEndDateTime = reservationDateTime
            )

            assertEquals(expectedConcertDetails.size, concertDetails.size)
        }
    }

    @Nested
    @DisplayName("콘서트 상세 조회")
    inner class GetByConcertDetailIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertDetailId = 1L
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = 1L,
                reservationStartDateTime = LocalDateTime.now().minusDays(10),
                reservationEndDateTime = LocalDateTime.now().plusDays(10)
            )
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val concertDetail = concertDetailService.getByConcertDetailId(concertDetailId)

            //then
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)
            assertEquals(expectedConcertDetail.concertDetailId, concertDetail.concertDetailId)
            assertEquals(expectedConcertDetail.reservationStartDateTime, concertDetail.reservationStartDateTime)
            assertEquals(expectedConcertDetail.reservationEndDateTime, concertDetail.reservationEndDateTime)
        }

        @Test
        fun `실패 (콘서트 상세가 존재하지 않는 경우)`() {
            //given
            val concertDetailId = 1L
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(null)

            //when
            val exception = assertThrows<ConcertDetailNotFoundException> {
                concertDetailService.getByConcertDetailId(concertDetailId)
            }

            //then
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)
            assertEquals("콘서트 상세가 존재하지 않습니다. concertDetailId : $concertDetailId", exception.message)
        }
    }
}