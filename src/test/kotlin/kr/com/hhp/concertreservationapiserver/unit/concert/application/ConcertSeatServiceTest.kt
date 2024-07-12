package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.ConcertSeatService
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatAlreadyReservedException
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatIsNotTemporaryStatusException
import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertSeatNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserIdMisMatchException
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
    inner class GetAllReservationStatusIsAvailableByConcertDetailId {
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
    inner class GetByConcertSeatIdTest {
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
            val exception = assertThrows<ConcertSeatNotFoundException> {
                concertSeatService.getByConcertSeatId(concertSeatId)
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals("ConcertSeat이 존재하지 않습니다. concertSeatId : $concertSeatId", exception.message)
        }
    }

    @Nested
    @DisplayName("임시예약 상태가 아닌 경우 예외처리")
    inner class ThrowExceptionIfStatusIsNotTemporaryTest {
        @Test
        fun `성공 (임시 예약 상태인 경우)`() {
            //given
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.T
            )

            //when
            concertSeatService.throwExceptionIfStatusIsNotTemporary(concertSeat)
        }

        @Test
        fun `실패 (임시 예약 상태가 아닌 경우)`() {
            //given
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.A
            )

            //when
            val exception = assertThrows<ConcertSeatIsNotTemporaryStatusException> {
                concertSeatService.throwExceptionIfStatusIsNotTemporary(concertSeat)
            }

            //then
            assertEquals("임시 예약된 좌석이 아닙니다.", exception.message)
        }
    }

    @Nested
    @DisplayName("임시 예약되어있는 좌석 예약 완료를 위해 결제")
    inner class PayForTemporaryReservedSeatToConfirmedReservedTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val userId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.T, userId = userId
            )
            val payedExceptedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.C, userId = userId
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId))
                .willReturn(expectedConcertSeat)
            given(concertSeatRepository.save(any())).willReturn(payedExceptedConcertSeat)

            // when
            val concertSeat = concertSeatService.payForTemporaryReservedSeatToConfirmedReserved(
                concertSeatId = concertDetailId,
                userId = userId
            )

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertSeatRepository).should().save(any())
        }

        @Test
        fun `실패 (임시 예약 좌석이 아닌 경우)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val userId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.A, userId = userId
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId))
                .willReturn(expectedConcertSeat)

            // when
            val exception = assertThrows<ConcertSeatIsNotTemporaryStatusException> {
                concertSeatService.payForTemporaryReservedSeatToConfirmedReserved(
                    concertSeatId = concertDetailId,
                    userId = userId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals("임시 예약된 좌석이 아닙니다.", exception.message)
        }

        @Test
        fun `실패 (예약한 사용자와, 현재 사용자가 다른 경우)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val userId = 1L
            val concertSeatUserId = 2L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.T, userId = concertSeatUserId
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId))
                .willReturn(expectedConcertSeat)

            // when
            val exception = assertThrows<UserIdMisMatchException> {
                concertSeatService.payForTemporaryReservedSeatToConfirmedReserved(
                    concertSeatId = concertDetailId,
                    userId = userId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals("유저Id가 일치하지 않습니다. userId : ${userId}, concertSeatId.userId : $concertSeatUserId", exception.message)
        }
    }


    @Nested
    @DisplayName("임시 예약되어있는 좌석 예약 완료를 위해 결제")
    inner class ReserveSeatToTemporaryTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val userId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.A
            )
            val updatedExpectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.T
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertSeatRepository.save(any())).willReturn(updatedExpectedConcertSeat)

            //when
            val concertSeat = concertSeatService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertSeatRepository).should().save(any())
            assertEquals(updatedExpectedConcertSeat.reservationStatus, concertSeat.reservationStatus)
        }

        @Test
        fun `실패 (이미 예약된 좌석인 경우)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val price = 50000
            val userId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = price,
                reservationStatus = ConcertReservationStatus.T
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)

            //when
            val exception = assertThrows<ConcertSeatAlreadyReservedException> {
                concertSeatService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            assertEquals("이미 예약된 좌석입니다.", exception.message)
        }
    }


    @Nested
    @DisplayName("예약 가능 상태가 아닌 경우 예외처리")
    inner class ThrowExceptionIfStatusIsNotAvailableTest {
        @Test
        fun `성공 (예약 가능 상태인 경우)`() {
            //given
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.A
            )

            //when
            concertSeatService.throwExceptionIfStatusIsNotAvailable(concertSeat)
        }

        @Test
        fun `실패 (예약 가능 상태가 아닌 경우)`() {
            //given
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.C
            )

            //when
            val exception = assertThrows<ConcertSeatAlreadyReservedException> {
                concertSeatService.throwExceptionIfStatusIsNotAvailable(concertSeat)
            }

            //then
            assertEquals("이미 예약된 좌석입니다.", exception.message)
        }
    }

    @Nested
    @DisplayName("좌석 예약한 사용자와 결제한 사용자가 다른 경우 예외처리")
    inner class ThrowExceptionIfMisMatchUserIdTest {
        @Test
        fun `성공 (사용자가 모두 동일인인 경우)`() {
            //given
            val userId = 1L
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.A,
                userId = userId
            )

            //when
            concertSeatService.throwExceptionIfMisMatchUserId(concertSeat, userId)
        }

        @Test
        fun `실패 (예약 가능 상태가 아닌 경우)`() {
            //given
            val concertUserId = 1L
            val userId = 2L
            val concertSeat = ConcertSeatEntity(
                concertDetailId = 1L,
                seatNumber = 1,
                price = 50000,
                reservationStatus = ConcertReservationStatus.C,
                userId = concertUserId
            )

            //when
            val exception = assertThrows<UserIdMisMatchException> {
                concertSeatService.throwExceptionIfMisMatchUserId(concertSeat, userId)
            }

            //then
            assertEquals("유저Id가 일치하지 않습니다. userId : ${userId}, concertSeatId.userId : ${concertSeat.userId}", exception.message)
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