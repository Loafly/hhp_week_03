package kr.com.hhp.concertreservationapiserver.integration.token.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.application.TokenFacade
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueStatus
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class TokenQueueFacadeTest {

    @Autowired
    private lateinit var tokenFacade: TokenFacade

    @Autowired
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Nested
    @DisplayName("토큰 발급")
    inner class CreateTokenQueueTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())

            //when
            val tokenQueue = tokenFacade.createToken(userId = user.userId!!)

            //then
            assertNotNull(tokenQueue)
            assertNotNull(tokenQueue.token)
        }

        @Test
        fun `실패 (유저가 존재하지 않는 경우)`() {
            //given
            val userId = 0L

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.createToken(userId = userId)
            }

            //then
            assertEquals(ErrorCode.USER_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.USER_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("토큰 정보 조회")
    inner class GetTokenQueueInfoTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val tokenInfo = tokenFacade.getTokenInfo(token = tokenQueue.token)

            //then
            assertNotNull(tokenInfo)
            assertEquals(tokenQueue.userId, tokenInfo.userId)
            assertEquals(tokenQueue.status.toString(), tokenInfo.status)
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.getTokenInfo(token = tokenQueue.token + "-invalid")
            }

            //then
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("토큰 유효성 검사")
    inner class VerifyTokenQueueTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            tokenFacade.verifyToken(token = tokenQueue.token)

            //then
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.verifyToken(token = tokenQueue.token + "-invalid")
            }

            //then
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (토큰이 null인 경우)`() {
            //given

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.verifyToken(token = null)
            }

            //then
            assertEquals(ErrorCode.TOKEN_IS_NULL.message, exception.message)
            assertEquals(ErrorCode.TOKEN_IS_NULL.code, exception.code)
        }
    }

    @Nested
    @DisplayName("토큰 유효성 검사 (Progress 상태인지)")
    inner class VerifyTokenQueueIsInProgressTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

            //when
            tokenFacade.verifyTokenIsInProgress(token = tokenQueue.token)

            //then
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.verifyTokenIsInProgress(token = tokenQueue.token + "-invalid")
            }

            //then
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (토큰값이 null인 경우)`() {
            //given

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.verifyTokenIsInProgress(token = null)
            }

            //then
            assertEquals(ErrorCode.TOKEN_IS_NULL.message, exception.message)
            assertEquals(ErrorCode.TOKEN_IS_NULL.code, exception.code)
        }

        @Test
        fun `실패 (토큰큐 상태가 Progress가 아닌 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val exception = assertThrows<CustomException> {
                tokenFacade.verifyTokenIsInProgress(token = tokenQueue.token)
            }

            //then
            assertEquals(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message, exception.message)
            assertEquals(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code, exception.code)
        }
    }


}