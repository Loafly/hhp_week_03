package kr.com.hhp.concertreservationapiserver.unit.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatPaymentHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatPaymentHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertService
import org.junit.jupiter.api.Assertions.assertEquals
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

@ExtendWith(MockitoExtension::class)
class ConcertServiceTest {

    @Mock
    private lateinit var concertRepository: ConcertRepository

    @Mock
    private lateinit var concertDetailRepository: ConcertDetailRepository

    @Mock
    private lateinit var concertSeatRepository: ConcertSeatRepository

    @Mock
    private lateinit var concertReservationHistoryRepository: ConcertReservationHistoryRepository

    @Mock
    private lateinit var concertSeatPaymentHistoryRepository: ConcertSeatPaymentHistoryRepository

    @InjectMocks
    private lateinit var concertService: ConcertService

    @Nested
    @DisplayName("예약 가능 날짜 조회")
    inner class GetAllAvailableReservationDetailTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertId = 1L
            val reservationDateTime = LocalDateTime.now()
            val expectedConcert = ConcertEntity(concertId)
            val expectedConcertDetails =
                listOf(
                    ConcertDetailEntity(
                        concertDetailId = 1L,
                        concertId = concertId,
                        reservationStartDateTime = LocalDateTime.now().minusDays(1),
                        reservationEndDateTime = LocalDateTime.now().plusDays(1)
                    ),
                    ConcertDetailEntity(
                        concertDetailId = 2L,
                        concertId = concertId,
                        reservationStartDateTime = LocalDateTime.now().minusDays(1),
                        reservationEndDateTime = LocalDateTime.now().plusDays(1)
                    ),
                    ConcertDetailEntity(
                        concertDetailId = 3L,
                        concertId = concertId,
                        reservationStartDateTime = LocalDateTime.now().minusDays(1),
                        reservationEndDateTime = LocalDateTime.now().plusDays(1)
                    )
                )

            given(concertRepository.findByConcertId(concertId)).willReturn(expectedConcert)
            given(
                concertDetailRepository.findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
                    concertId = concertId,
                    remainingSeatCount = 0,
                    reservationStartDateTime = reservationDateTime,
                    reservationEndDateTime = reservationDateTime
                )
            ).willReturn(expectedConcertDetails)

            //when
            val concertDetails = concertService.getAllAvailableReservationDetail(
                concertId = concertId, reservationDateTime = reservationDateTime
            )

            //then
            then(concertRepository).should().findByConcertId(concertId)
            then(concertDetailRepository).should()
                .findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
                    concertId = concertId,
                    remainingSeatCount = 0,
                    reservationStartDateTime = reservationDateTime,
                    reservationEndDateTime = reservationDateTime
                )

            assertEquals(expectedConcertDetails, concertDetails)
        }

        @Test
        fun `실패 (콘서트가 존재하지 않는 경우)`() {
            //given
            val concertId = 1L
            val reservationDateTime = LocalDateTime.now()

            given(concertRepository.findByConcertId(concertId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                concertService.getAllAvailableReservationDetail(
                    concertId = concertId, reservationDateTime = reservationDateTime
                )
            }

            //then
            then(concertRepository).should().findByConcertId(concertId)
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("예약 가능 좌석 조회")
    inner class GetAllAvailableReservationSeatTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertId = 1L
            val concertDetailId = 1L
            val concertDetail = ConcertDetailEntity(
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(1)
            )
            val expectedConcertSeats = listOf(
                ConcertSeatEntity(
                    concertSeatId = 1L,
                    concertDetailId = concertDetailId,
                    seatNumber = 1,
                    price = 50000
                ),
                ConcertSeatEntity(
                    concertSeatId = 2L,
                    concertDetailId = concertDetailId,
                    seatNumber = 2,
                    price = 50000
                ),
                ConcertSeatEntity(
                    concertSeatId = 3L,
                    concertDetailId = concertDetailId,
                    seatNumber = 3,
                    price = 50000
                )
            )

            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(concertDetail)
            given(
                concertSeatRepository.findAllByConcertDetailIdAndReservationStatus(
                    concertDetailId = concertDetailId, reservationStatus = ConcertReservationStatus.A
                )
            )
                .willReturn(expectedConcertSeats)

            //when
            val concertSeats = concertService.getAllAvailableReservationSeat(concertDetailId)

            //then
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)
            then(concertSeatRepository).should().findAllByConcertDetailIdAndReservationStatus(
                concertDetailId = concertDetailId, reservationStatus = ConcertReservationStatus.A
            )

            assertEquals(expectedConcertSeats, concertSeats)
        }

        @Test
        fun `실패 (콘서트 상세가 없는 경우)`() {
            //given
            val concertDetailId = 1L
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException>{
                concertService.getAllAvailableReservationSeat(concertDetailId)
            }

            //then
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("좌석 임시 예약")
    inner class ReserveSeatToTemporaryTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val userId = 1L
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = 5000
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            val expectedUpdatedConcertDetail = ConcertDetailEntity(
                concertDetailId = expectedConcertDetail.concertDetailId,
                concertId = expectedConcertDetail.concertId,
                reservationStartDateTime = expectedConcertDetail.reservationStartDateTime,
                reservationEndDateTime = expectedConcertDetail.reservationEndDateTime,
                remainingSeatCount = expectedConcertDetail.remainingSeatCount - 1
            )

            val expectedUpdatedConcertSeat = ConcertSeatEntity(
                concertSeatId = expectedConcertSeat.concertSeatId,
                concertDetailId = expectedConcertSeat.concertDetailId,
                seatNumber = expectedConcertSeat.seatNumber,
                price = expectedConcertSeat.price,
                userId = userId,
                reservationStatus = ConcertReservationStatus.T
            )

            given(concertDetailRepository.save(any())).willReturn(expectedUpdatedConcertDetail)
            given(concertSeatRepository.save(any())).willReturn(expectedUpdatedConcertSeat)

            //when
            val concertSeat =
                concertService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)

            //then
            then(concertSeatRepository).should().findByConcertSeatIdWithSLock(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            then(concertDetailRepository).should().save(any())
            then(concertSeatRepository).should().save(any())

            assertEquals(expectedUpdatedConcertSeat, concertSeat)
        }

        @Test
        fun `실패 (좌석이 존재하지 않는 경우)`() {
            //given
            val userId = 1L
            val concertSeatId = 1L

            given(concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                concertService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)
            }


            //then
            then(concertSeatRepository).should().findByConcertSeatIdWithSLock(concertSeatId)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (콘서트 상세가 존재하지 않는 경우)`() {
            //given
            val userId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = 5000
            )

            given(concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                concertService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatIdWithSLock(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약 기간보다 빠른 경우)`() {
            //given
            val userId = 1L
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = 5000
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().plusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(2)
            )

            given(concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)
            }


            //then
            then(concertSeatRepository).should().findByConcertSeatIdWithSLock(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.message, exception.message)
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약 기간이 지난 경우)`() {
            //given
            val userId = 1L
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId, concertDetailId = concertDetailId, seatNumber = 1, price = 5000
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(2),
                reservationEndDateTime = LocalDateTime.now().minusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.reserveSeatToTemporary(concertSeatId = concertSeatId, userId = userId)
            }


            //then
            then(concertSeatRepository).should().findByConcertSeatIdWithSLock(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.message, exception.message)
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.code, exception.code)
        }
    }

    @Nested
    @DisplayName("예약된 좌석 결제")
    inner class PayForTemporaryReservedSeatToConfirmedReservationTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = userId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.T
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            val expectedUpdatedConcertSeat = ConcertSeatEntity(
                concertSeatId = expectedConcertSeat.concertSeatId,
                userId = expectedConcertSeat.userId,
                concertDetailId = expectedConcertSeat.concertDetailId,
                seatNumber = expectedConcertSeat.seatNumber,
                price = expectedConcertSeat.price,
                reservationStatus = ConcertReservationStatus.C
            )

            given(concertSeatRepository.save(any())).willReturn(expectedUpdatedConcertSeat)

            //when
            val concertSeat = concertService.payForTemporaryReservedSeatToConfirmedReservation(
                concertSeatId = concertSeatId, userId = userId, walletId = walletId
            )

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)
            then(concertSeatRepository).should().save(any())

            assertEquals(expectedUpdatedConcertSeat, concertSeat)
        }

        @Test
        fun `실패 (좌석이 없는 경우)`() {
            //given
            val concertSeatId = 1L
            val userId = 1L
            val walletId = 1L

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException>{
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)

            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (콘서트 상세가 없는 경우)`() {
            //given
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = userId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.T
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (예약 기간이 시작되지 않은 경우)`() {
            //given
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = userId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.T
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().plusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(2)
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.message, exception.message)
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.code, exception.code)
        }

        @Test
        fun `실패 (예약 기간이 끝난 경우)`() {
            //given
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = userId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.T
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(2),
                reservationEndDateTime = LocalDateTime.now().minusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.message, exception.message)
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약된 좌석이 아닌 경우)`() {
            //given
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = userId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.A
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.code, exception.code)
        }

        @Test
        fun `실패 (사용자가 다른 경우)`() {
            //given
            val concertId = 1L
            val concertSeatId = 1L
            val concertDetailId = 1L
            val userId = 1L
            val walletId = 1L
            val concertUserId = 2L

            val expectedConcertSeat = ConcertSeatEntity(
                concertSeatId = concertSeatId,
                userId = concertUserId,
                concertDetailId = concertDetailId,
                seatNumber = 1,
                price = 5000,
                reservationStatus = ConcertReservationStatus.T
            )
            val expectedConcertDetail = ConcertDetailEntity(
                concertDetailId = concertDetailId,
                concertId = concertId,
                reservationStartDateTime = LocalDateTime.now().minusDays(1),
                reservationEndDateTime = LocalDateTime.now().plusDays(1)
            )

            given(concertSeatRepository.findByConcertSeatId(concertSeatId)).willReturn(expectedConcertSeat)
            given(concertDetailRepository.findByConcertDetailId(concertDetailId)).willReturn(expectedConcertDetail)

            //when
            val exception = assertThrows<CustomException> {
                concertService.payForTemporaryReservedSeatToConfirmedReservation(
                    concertSeatId = concertSeatId, userId = userId, walletId = walletId
                )
            }

            //then
            then(concertSeatRepository).should().findByConcertSeatId(concertSeatId)
            then(concertDetailRepository).should().findByConcertDetailId(concertDetailId)

            assertEquals(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.code, exception.code)
        }

    }
}