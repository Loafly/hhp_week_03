package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertSeatService
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.then
import java.time.LocalDateTime
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ConcertSeatServiceTest {

    @Mock
    private lateinit var concertSeatRepository: ConcertSeatRepository

    @InjectMocks
    private lateinit var concertSeatService: ConcertSeatService

    @Nested
    @DisplayName("특정 콘서트/날짜에 예약 가능한 좌석 조회")
    inner class GetAllReservationStatusIsAvailableByConcertDtoDetailId {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertDetailId = 1L
            val price = 50000

            val expectedConcertSeats = listOf(
                ConcertSeatEntity(concertDetailId = concertDetailId, seatNumber = 1, price = price),
                ConcertSeatEntity(concertDetailId = concertDetailId, seatNumber = 2, price = price),
                ConcertSeatEntity(concertDetailId = concertDetailId, seatNumber = 3, price = price)
            )

            given(
                concertSeatRepository.findAllByConcertDetailIdAndReservationStatus(
                    concertDetailId = concertDetailId,
                    reservationStatus = ConcertReservationStatus.A
                )
            ).willReturn(expectedConcertSeats)

            //when
            val concertSeats =
                concertSeatService.getAllReservationStatusIsAvailableByConcertDetailId(
                    concertDetailId
                )

            //then
            then(concertSeatRepository).should()
                .findAllByConcertDetailIdAndReservationStatus(
                    concertDetailId = concertDetailId,
                    reservationStatus = ConcertReservationStatus.A
                )

            assertEquals(expectedConcertSeats.size, concertSeats.size)
        }
    }

    @Nested
    @DisplayName("콘서트 좌석 조회")
    inner class GetByConcertDtoSeatIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price
            )
            given(concertSeatRepository.findByConcertSeatId(concertSeatId))
                .willReturn(expectedConcertSeat)

            //when
            val concertSeat = concertSeatService.getByConcertSeatId(concertSeatId)

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals(expectedConcertSeat.concertSeatId, concertSeat.concertSeatId)
        }

        @Test
        fun `실패 (콘서트 좌석이 존재하지 않는 경우)`() {
            //given
            val concertSeatId = 1L
            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                concertSeatService.getByConcertSeatId(concertSeatId)
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("임시예약 기간이 만료된 경우 초기화")
    inner class ReleaseExpiredReservationsTest {
        fun `성공 (정상 케이스)`() {
            //given
            val expectedConcertSeats = listOf(
                ConcertSeatEntity(
                    userId = 1L, concertDetailId = 1L, reservationStatus = ConcertReservationStatus.T,
                    price = 1000, seatNumber = 1, updatedAt = LocalDateTime.now().minusDays(1)
                ),
                ConcertSeatEntity(
                    userId = 2L, concertDetailId = 1L, reservationStatus = ConcertReservationStatus.T,
                    price = 1000, seatNumber = 2, updatedAt = LocalDateTime.now().minusDays(1)
                ),
            )
            val updatedExpectedConcertSeats = listOf(
                ConcertSeatEntity(
                    concertDetailId = 1L, reservationStatus = ConcertReservationStatus.A,
                    price = 1000, seatNumber = 1, updatedAt = LocalDateTime.now()
                ),
                ConcertSeatEntity(
                    concertDetailId = 1L, reservationStatus = ConcertReservationStatus.A,
                    price = 1000, seatNumber = 2, updatedAt = LocalDateTime.now()
                ),
            )

            given(concertSeatRepository.findAllByReservationStatusAndUpdatedAtIsAfter(
                ConcertReservationStatus.T, LocalDateTime.now().minusMinutes(30)
            )).willReturn(expectedConcertSeats)
            given(concertSeatRepository.saveAll(any())).willReturn(updatedExpectedConcertSeats)

            //when
            concertSeatService.releaseExpiredReservations()

            //then
            then(concertSeatRepository).should().findAllByReservationStatusAndUpdatedAtIsAfter(
                ConcertReservationStatus.T, LocalDateTime.now().minusMinutes(30)
            )
            then(concertSeatRepository).should().saveAll(any())
        }
    }

    @Nested
    @DisplayName("임시예약 기간이 리스트 조회")
    inner class GetAllExpiredReservationTemporaries {
        fun `성공 (정상 케이스)`() {
            //given
            val expectedConcertSeats = listOf(
                ConcertSeatEntity(
                    userId = 1L, concertDetailId = 1L, reservationStatus = ConcertReservationStatus.T,
                    price = 1000, seatNumber = 1, updatedAt = LocalDateTime.now().minusDays(1)
                ),
                ConcertSeatEntity(
                    userId = 2L, concertDetailId = 1L, reservationStatus = ConcertReservationStatus.T,
                    price = 1000, seatNumber = 2, updatedAt = LocalDateTime.now().minusDays(1)
                ),
            )
            given(concertSeatRepository.findAllByReservationStatusAndUpdatedAtIsAfter(
                ConcertReservationStatus.T, LocalDateTime.now().minusMinutes(30)
            )).willReturn(expectedConcertSeats)

            //when
            val concertSeats = concertSeatService.getAllExpiredReservationTemporaries()

            //then
            then(concertSeatRepository).should().findAllByReservationStatusAndUpdatedAtIsAfter(
                ConcertReservationStatus.T, LocalDateTime.now().minusMinutes(30)
            )
            assertEquals(expectedConcertSeats.size, concertSeats.size)
        }
    }
}