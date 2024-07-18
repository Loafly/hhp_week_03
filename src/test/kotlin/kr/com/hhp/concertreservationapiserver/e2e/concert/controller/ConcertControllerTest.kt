package kr.com.hhp.concertreservationapiserver.e2e.concert.controller

import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ConcertControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    private lateinit var concertRepository: ConcertRepository

    @Autowired
    private lateinit var concertDetailRepository: ConcertDetailRepository

    @Autowired
    private lateinit var concertSeatRepository: ConcertSeatRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Nested
    @DisplayName("콘서트 상세 조회")
    inner class GetConcertDetailTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/${concert.concertId}/details")
                    .header("token", tokenQueue.token)
                    .param("reservationDateTime", LocalDateTime.now().toString())
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].concertId").value(concertDetail.concertId))
                .andExpect(jsonPath("$[0].totalSeatCount").value(concertDetail.totalSeatCount))
                .andExpect(jsonPath("$[0].remainingSeatCount").value(concertDetail.remainingSeatCount))
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val invalidToken = tokenQueue.token + "-notFound"
            val concert = concertRepository.save(ConcertEntity())

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/${concert.concertId}/details")
                    .header("token", invalidToken)
                    .param("reservationDateTime", LocalDateTime.now().toString())
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_NOT_FOUND.code))
        }

        @Test
        fun `실패 (토큰이 null 경우)`() {
            // given
            val concert = concertRepository.save(ConcertEntity())

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/${concert.concertId}/details")
                    .param("reservationDateTime", LocalDateTime.now().toString())
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_IS_NULL.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_IS_NULL.code))
        }

        @Test
        fun `실패 (유효하지 않은 토큰인 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.W))
            val invalidToken = tokenQueue.token
            val concert = concertRepository.save(ConcertEntity())

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/${concert.concertId}/details")
                    .header("token", invalidToken)
                    .param("reservationDateTime", LocalDateTime.now().toString())
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code))
        }

        @Test
        fun `실패 (콘서트가 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concertId = 2L

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/${concertId}/details")
                    .header("token", tokenQueue.token)
                    .param("reservationDateTime", LocalDateTime.now().toString())
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_NOT_FOUND.code))
        }
    }

    @Nested
    @DisplayName("콘서트 좌석 조회")
    inner class GetConcertSeatTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/details/${concertDetail.concertDetailId}/seats")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].concertSeatId").value(concertSeat.concertSeatId))
                .andExpect(jsonPath("$[0].seatNumber").value(concertSeat.seatNumber))
                .andExpect(jsonPath("$[0].price").value(concertSeat.price))
                .andExpect(jsonPath("$[0].reservationStatus").value(concertSeat.reservationStatus.toString()))
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val invalidToken = tokenQueue.token + "-notFound"
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/details/${concertDetail.concertDetailId}/seats")
                    .header("token", invalidToken)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_NOT_FOUND.code))
        }

        @Test
        fun `실패 (토큰이 null 경우)`() {
            // given
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/details/${concertDetail.concertDetailId}/seats")
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_IS_NULL.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_IS_NULL.code))
        }

        @Test
        fun `실패 (유효하지 않은 토큰인 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.W))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/details/${concertDetail.concertDetailId}/seats")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code))
        }

        @Test
        fun `실패 (콘서트 상세가 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val invalidToken = tokenQueue.token
            val concertDetailId = 1L

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/concerts/details/${concertDetailId}/seats")
                    .header("token", invalidToken)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_DETAIL_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_DETAIL_NOT_FOUND.code))
        }
    }

    @Nested
    @DisplayName("콘서트 좌석 임시 예약")
    inner class ReservationSeatTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.concertSeatId").value(concertSeat.concertSeatId))
                .andExpect(jsonPath("$.seatNumber").value(concertSeat.seatNumber))
                .andExpect(jsonPath("$.price").value(concertSeat.price))
                .andExpect(jsonPath("$.reservationStatus").value(concertSeat.reservationStatus.toString()))
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val invalidToken = tokenQueue.token + "-invalid"
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", invalidToken)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_NOT_FOUND.code))
        }

        @Test
        fun `실패 (토큰이 null 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_IS_NULL.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_IS_NULL.code))
        }

        @Test
        fun `실패 (유효하지 않은 토큰인 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.W))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code))
        }

        @Test
        fun `실패 (콘서트 좌석 임시 예약 기간이 아닌 경우 (시작 이전))`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().plusDays(5),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY.code))
        }

        @Test
        fun `실패 (콘서트 좌석 임시 예약 기간이 아닌 경우 (종료 이후))`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().minusDays(5)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE.code))
        }

        @Test
        fun `실패 (콘서트 좌석이 이미 예매된 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000,
                    reservationStatus = ConcertReservationStatus.T
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_SEAT_ALREADY_RESERVED.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_SEAT_ALREADY_RESERVED.code))
        }

        @Test
        fun `실패 (콘서트 좌석이 없는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concertSeatId = 1
            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeatId}/reservation")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_SEAT_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_SEAT_NOT_FOUND.code))
        }
    }

    @Nested
    @DisplayName("콘서트 좌석 결제")
    inner class PaymentSeatTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000,
                    reservationStatus = ConcertReservationStatus.T
                )
            )
            walletRepository.save(
                WalletEntity(
                    userId = user.userId!!,
                    balance = 10000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
                    .header("token", tokenQueue.token)
            )

            //then
            perform.andExpect(status().isOk)
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val invalidToken = tokenQueue.token + "-invalid"
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
                    .header("token", invalidToken)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_NOT_FOUND.code))
        }

        @Test
        fun `실패 (토큰이 null 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_IS_NULL.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_IS_NULL.code))
        }

        @Test
        fun `실패 (유효하지 않은 토큰인 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.W))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code))
        }

        @Test
        fun `실패 (임시 예약된 좌석이 아닌 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = user.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000,
                    reservationStatus = ConcertReservationStatus.A
                )
            )
            walletRepository.save(
                WalletEntity(
                    userId = user.userId!!,
                    balance = 10000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS.code))
        }

        @Test
        fun `실패 (임시 예약한 사용자가 아닌 경우)`() {
            // given
            val user = userRepository.save(UserEntity())
            val concertUser = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    totalSeatCount = 50,
                    remainingSeatCount = 50,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    userId = concertUser.userId!!,
                    concertDetailId = concertDetail.concertDetailId!!,
                    seatNumber = 1,
                    price = 5000,
                    reservationStatus = ConcertReservationStatus.T
                )
            )
            walletRepository.save(
                WalletEntity(
                    userId = user.userId!!,
                    balance = 10000
                )
            )

            // when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/concerts/details/seats/${concertSeat.concertSeatId}/payment")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH.code))
        }
    }

}