package kr.com.hhp.concertreservationapiserver.unit.token.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import org.junit.jupiter.api.Assertions.assertEquals
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
import java.util.*

@ExtendWith(MockitoExtension::class)
class TokenQueueServiceTest {

    @Mock
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @InjectMocks
    private lateinit var tokenQueueService: TokenQueueService

    @Nested
    @DisplayName("진행중 상태로 업데이트 ")
    inner class UpdateWaitingAllTokenToInProgressTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val maxInProgressSize = 100L
            val expectedValue = 100L
            given(tokenQueueRepository.activateTokens(maxInProgressSize)).willReturn(expectedValue)

            //when
            val value =
                tokenQueueService.updateWaitingAllTokenToInProgress(maxInProgressSize)

            //then
            then(tokenQueueRepository).should().activateTokens(maxInProgressSize)
            assertEquals(expectedValue, value)

        }
    }

    @Nested
    @DisplayName("토큰 생성")
    inner class CreateTokenTest {
        @Test
        fun `성공 (정상 케이스)`() {
            val userId = 1L
            val expectedToken = UUID.randomUUID().toString()
            given(tokenQueueRepository.addWaitingToken(userId)).willReturn(expectedToken)

            //when
            val token =
                tokenQueueService.createToken(userId)

            //then
            then(tokenQueueRepository).should().addWaitingToken(userId)
            assertEquals(expectedToken, token)
        }
    }

    @Nested
    @DisplayName("유저 정보 조회")
    inner class GetUserIdByToken {
        @Test
        fun `성공 (정상 케이스)`() {
            val expectedUserId = 1L
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.getUserIdByToken(token)).willReturn(expectedUserId)


            //when
            val userId = tokenQueueService.getUserIdByToken(token)

            //then
            then(tokenQueueRepository).should().getUserIdByToken(token)
            assertEquals(expectedUserId, userId)
        }

        @Test
        fun `실패 (토큰이 없는 경우)`() {
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.getUserIdByToken(token)).willReturn(null)


            //when
            val exception = assertThrows<CustomException> {
                tokenQueueService.getUserIdByToken(token)
            }

            //then
            then(tokenQueueRepository).should().getUserIdByToken(token)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.TOKEN_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("토큰 랭킹 조회")
    inner class GetRankByTokenTest {
        @Test
        fun `성공 (정상 케이스)`() {
            val expectedRank = 10L
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.getWaitingPosition(token)).willReturn(expectedRank)

            //when
            val rank = tokenQueueService.getRankByToken(token)

            //then
            then(tokenQueueRepository).should().getWaitingPosition(token)
            assertEquals(expectedRank, rank)

        }
    }


    @Nested
    @DisplayName("토큰 활성화 여부 조회")
    inner class IsActiveTokenTest {
        @Test
        fun `성공 (정상 케이스)`() {
            val expectedActive = true
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.isActiveToken(token)).willReturn(expectedActive)

            //when
            val isActive = tokenQueueService.isActiveToken(token)

            //then
            then(tokenQueueRepository).should().isActiveToken(token)
            assertEquals(expectedActive, isActive)

        }
    }

    @Nested
    @DisplayName("토큰 활성화에 따른 예외 처리")
    inner class ThrowExceptionIfStatusIsNotInProgressTest {
        @Test
        fun `성공 (정상 케이스)`() {
            val expectedActive = true
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.isActiveToken(token)).willReturn(expectedActive)

            //when
            tokenQueueService.throwExceptionIfStatusIsNotInProgress(token)

            //then
            then(tokenQueueRepository).should().isActiveToken(token)
        }

        @Test
        fun `실패 (진행중 상태가 아닌 경우)`() {
            val expectedActive = false
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.isActiveToken(token)).willReturn(expectedActive)

            //when
            val exception = assertThrows<CustomException> {
                tokenQueueService.throwExceptionIfStatusIsNotInProgress(token)
            }

            //then
            then(tokenQueueRepository).should().isActiveToken(token)
            assertEquals(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.message, exception.message)
            assertEquals(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS.code, exception.code)
        }
    }


    @Nested
    @DisplayName("토큰 삭제")
    inner class DeleteActiveToken {
        @Test
        fun `성공 (정상 케이스)`() {
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.deleteActiveToken(token)).willAnswer{}

            //when
            tokenQueueService.deleteActiveToken(token)

            //then
            then(tokenQueueRepository).should().deleteActiveToken(token)
        }
    }

}