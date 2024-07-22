package kr.com.hhp.concertreservationapiserver.integration.concert.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.token.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueStatus
import kr.com.hhp.concertreservationapiserver.user.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    private lateinit var concertFacade: ConcertFacade

    @Autowired
    lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var concertRepository: ConcertRepository

    @Autowired
    lateinit var concertDetailRepository: ConcertDetailRepository

    @Autowired
    lateinit var concertSeatRepository: ConcertSeatRepository

    @Autowired
    lateinit var walletRepository: WalletRepository

    @Nested
    @DisplayName("예약 가능 시간 리스트 조회")
    inner class GetAllAvailableReservationDetailTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )

            //when
            val reservationDetails = concertFacade.getAllAvailableReservationDetail(
                token = tokenQueue.token, concertId = concert.concertId!!, reservationDateTime = LocalDateTime.now()
            )

            val reservationDetail = reservationDetails[0]

            //then
            assertEquals(1, reservationDetails.size)
            assertNotNull(reservationDetail)
            assertEquals(concertDetail.concertId, reservationDetail.concertId)
            assertEquals(concertDetail.reservationStartDateTime, reservationDetail.reservationStartDateTime)
            assertEquals(concertDetail.reservationEndDateTime, reservationDetail.reservationEndDateTime)
        }

        @Test
        fun `실패 (콘서트가 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concertId = 1L

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.getAllAvailableReservationDetail(
                    token = tokenQueue.token, concertId = concertId, reservationDateTime = LocalDateTime.now()
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.code, exception.code)
        }
    }



    @Nested
    @DisplayName("예약 가능 좌석 리스트 조회")
    inner class GetAllAvailableReservationSeatTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )

            //when
            val availableReservationSeats = concertFacade.getAllAvailableReservationSeat(
                token = tokenQueue.token, concertDetailId = concertDetail.concertDetailId!!
            )
            val availableReservationSeat = availableReservationSeats[0]

            //then
            assertEquals(1, availableReservationSeats.size)
            assertNotNull(availableReservationSeat)
            assertEquals(concertSeat.concertSeatId, availableReservationSeat.concertSeatId)
            assertEquals(concertSeat.reservationStatus.toString(), availableReservationSeat.reservationStatus)
            assertEquals(concertSeat.seatNumber, availableReservationSeat.seatNumber)
            assertEquals(concertSeat.price, availableReservationSeat.price)
        }

        @Test
        fun `실패 (콘서트 상세가 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concertDetailId = 1L

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.getAllAvailableReservationSeat(
                    token = tokenQueue.token, concertDetailId = concertDetailId
                )
            }

            //then
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
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )

            //when
            val temporaryConcertSeat = concertFacade.reserveSeatToTemporary(
                token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
            )

            //then
            assertNotNull(temporaryConcertSeat)
            assertEquals(concertSeat.concertSeatId, temporaryConcertSeat.concertSeatId)
            assertEquals(concertSeat.seatNumber, temporaryConcertSeat.seatNumber)
            assertEquals(concertSeat.price, temporaryConcertSeat.price)
            assertEquals(ConcertReservationStatus.T.toString(), temporaryConcertSeat.reservationStatus)
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.reserveSeatToTemporary(
                    token = (tokenQueue.token + "-invalid"), concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (콘서트 좌석이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concertSeatId = 1L

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.reserveSeatToTemporary(
                    token = (tokenQueue.token), concertSeatId = concertSeatId
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (콘서트 상세가 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concertDetailId = 1L
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetailId, price = 1000
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.reserveSeatToTemporary(
                    token = (tokenQueue.token), concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_DETAIL_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약 기간이 아닌 경우 (시작 이전))`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().plusDays(5),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.reserveSeatToTemporary(
                    token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.message, exception.message)
            assertEquals(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약 기간이 아닌 경우 (종료 이후))`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().minusDays(5)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.reserveSeatToTemporary(
                    token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
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
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1,
                    reservationStatus = ConcertReservationStatus.T,
                    concertDetailId = concertDetail.concertDetailId!!,
                    price = 1000,
                    userId = user.userId!!
                )
            )

            //when
            concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
            )
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1,
                    reservationStatus = ConcertReservationStatus.T,
                    concertDetailId = concertDetail.concertDetailId!!,
                    price = 1000,
                    userId = user.userId!!
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                    token = tokenQueue.token + "-invalid", concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (콘서트 좌석이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concertSeatId = 1L

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                    token = tokenQueue.token, concertSeatId = concertSeatId
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약된 좌석이 아닌 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1,
                    reservationStatus = ConcertReservationStatus.A,
                    concertDetailId = concertDetail.concertDetailId!!,
                    price = 1000,
                    userId = user.userId!!
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                    token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.message, exception.message)
            assertEquals(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.code, exception.code)
        }

        @Test
        fun `실패 (임시 예약된 사용자가 아닌 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))
            val concertUser = userRepository.save(UserEntity())

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1,
                    reservationStatus = ConcertReservationStatus.T,
                    concertDetailId = concertDetail.concertDetailId!!,
                    price = 1000,
                    userId = concertUser.userId!!
                )
            )

            //when
            val exception = assertThrows<CustomException> {
                concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                    token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                )
            }

            //then
            assertEquals(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.code, exception.code)
        }

    }


}