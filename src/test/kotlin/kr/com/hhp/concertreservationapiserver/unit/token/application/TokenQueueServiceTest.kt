package kr.com.hhp.concertreservationapiserver.unit.token.application

import kr.com.hhp.concertreservationapiserver.token.application.TokenQueueService
import kr.com.hhp.concertreservationapiserver.token.application.exception.TokenNotFoundException
import kr.com.hhp.concertreservationapiserver.token.application.exception.TokenStatusIsNotProgressException
import kr.com.hhp.concertreservationapiserver.token.domain.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueStatus
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
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class TokenQueueServiceTest {

    @Mock
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @InjectMocks
    private lateinit var tokenQueueService: TokenQueueService

    @Nested
    @DisplayName("토큰큐 생성")
    inner class CreateByUserIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val userId = 1L
            val expectedTokenQueue = TokenQueueEntity(userId = userId)
            given(tokenQueueRepository.save((any()))).willReturn(expectedTokenQueue)

            //when
            val createdTokenQueue = tokenQueueService.createByUserId(userId)

            //then
            then(tokenQueueRepository).should().save(any())
            assertEquals(expectedTokenQueue.userId, createdTokenQueue.userId)
        }
    }

    @Nested
    @DisplayName("토큰 큐 조회")
    inner class GetByTokenTest {
        @Test
        fun `성공 (정상 케이스)` () {
            //given
            val userId = 1L
            val token = UUID.randomUUID().toString()
            val expectedTokenQueue = TokenQueueEntity(userId = userId, token = token)
            given(tokenQueueRepository.findByToken(token)).willReturn(expectedTokenQueue)

            //when
            val tokenQueue = tokenQueueService.getByToken(token)

            //then
            then(tokenQueueRepository).should().findByToken(token)
            assertEquals(expectedTokenQueue.token, tokenQueue.token)
            assertEquals(expectedTokenQueue.userId, tokenQueue.userId)
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)` () {
            //given
            val userId = 1L
            val token = UUID.randomUUID().toString()
            given(tokenQueueRepository.findByToken(token)).willReturn(null)

            //when
            val exception = assertThrows<TokenNotFoundException> {
                tokenQueueService.getByToken(token)
            }

            //then
            then(tokenQueueRepository).should().findByToken(token)
            assertEquals("토큰이 존재하지 않습니다. token : $token", exception.message)
        }
    }


    @Nested
    @DisplayName("첫번째 대기 토큰 큐 조회")
    inner class GetNullAbleFirstWaitingTokenQueueTest {
        @Test
        fun `성공 (데이터가 있는 정상 케이스)`() {
            // given
            val userId = 1L
            val status = TokenQueueStatus.W
            val expectedTokenQueue = TokenQueueEntity(userId = userId, status = status)

            given(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)).willReturn(expectedTokenQueue)

            //when
            val firstWaitingTokenQueue = tokenQueueService.getNullAbleFirstWaitingTokenQueue()

            //then
            then(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W))
            assertNotNull(firstWaitingTokenQueue)
            assertEquals(expectedTokenQueue.status, firstWaitingTokenQueue.status)
        }

        @Test
        fun `성공 (데이터가 없는 정상 케이스)`() {
            // given
            given(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)).willReturn(null)

            //when
            val firstWaitingTokenQueue = tokenQueueService.getNullAbleFirstWaitingTokenQueue()

            //then
            then(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W))
            assertNull(firstWaitingTokenQueue)
        }
    }

    @Nested
    @DisplayName("진행상태인 토큰 큐 모두 조회")
    inner class GetAllByProgressTokenQueueTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val status = TokenQueueStatus.P
            val expectedTokenQueues = mutableListOf<TokenQueueEntity>()
            for (i in 1L..10L) {
                expectedTokenQueues.add(TokenQueueEntity(userId = i, status = status))
            }
            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(expectedTokenQueues)

            //when
            val tokenQueues = tokenQueueService.getAllByProgressTokenQueue()

            //then
            then(tokenQueueRepository).should().findAllByStatus(TokenQueueStatus.P)
            assertEquals(expectedTokenQueues.size, tokenQueues.size)
        }
    }

    @Nested
    @DisplayName("토큰 큐 전체 저장")
    inner class SaveAllTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val status = TokenQueueStatus.P
            val expectedTokenQueues = mutableListOf<TokenQueueEntity>()
            for (i in 1L..10L) {
                expectedTokenQueues.add(TokenQueueEntity(userId = i, status = status))
            }
            given(tokenQueueRepository.saveAll(any())).willReturn(expectedTokenQueues)

            //when
            val tokenQueues = tokenQueueService.saveAll(expectedTokenQueues)

            //then
            then(tokenQueueRepository).should().saveAll(any())
            assertEquals(expectedTokenQueues.size, tokenQueues.size)
        }

    }

    @Nested
    @DisplayName("토큰큐 Id 리스트로 토큰 큐 전체 조회")
    inner class GetAllByTokenQueueIdsTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val status = TokenQueueStatus.P
            val expectedTokenQueues = mutableListOf<TokenQueueEntity>()
            for (i in 1L..10L) {
                expectedTokenQueues.add(TokenQueueEntity(tokenQueueId = i, userId = i, status = status))
            }
            val tokenQueueIds = expectedTokenQueues.map { it.tokenQueueId!! }
            given(tokenQueueRepository.findAllByTokenQueueIdIn(any())).willReturn(expectedTokenQueues)

            //when
            val tokenQueues = tokenQueueService.getAllByTokenQueueIds(tokenQueueIds)

            //then
            then(tokenQueueRepository).should().findAllByTokenQueueIdIn(any())
            assertEquals(expectedTokenQueues.size, tokenQueues.size)
        }
    }


    @Nested
    @DisplayName("토큰큐 Id 리스트로 토큰 큐 전체 조회")
    inner class CalculateRemainingNumberTest {
        @Test
        fun `성공 (대기 중인 토큰 큐가 본인 혼자인 경우)`() {
            // given
            val currentWaitingTokenQueue = TokenQueueEntity(tokenQueueId = 1L, userId = 1L, status = TokenQueueStatus.W)

            //when
            val calculateRemainingNumber = tokenQueueService.calculateRemainingNumber(
                currentWaitingTokenQueue, currentWaitingTokenQueue
            )

            //then
            assertEquals(0L, calculateRemainingNumber)
        }

        @Test
        fun `성공 (토큰 큐 상태가 대기중이 아닌 경우)`() {
            // given
            val currentWaitingTokenQueue = TokenQueueEntity(userId = 1L, status = TokenQueueStatus.P)

            //when
            val calculateRemainingNumber = tokenQueueService.calculateRemainingNumber(
                null, currentWaitingTokenQueue
            )

            //then
            assertEquals(0L, calculateRemainingNumber)
        }

        @Test
        fun `성공 (앞에 10명이 있는 경우)`() {
            // given
            val firstWaitingTokenQueue = TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.W)
            val currentWaitingTokenQueue = TokenQueueEntity(tokenQueueId = 11, userId = 2L, status = TokenQueueStatus.W)

            //when
            val calculateRemainingNumber = tokenQueueService.calculateRemainingNumber(
                firstWaitingTokenQueue, currentWaitingTokenQueue
            )

            //then
            assertEquals(10L, calculateRemainingNumber)

        }
    }

    @Nested
    @DisplayName("대기중인 토큰 InProgress로 업데이트")
    inner class UpdateWaitingAllTokenToInProgress {
        @Test
        fun `성공 (Progress가 여유로운 정상 케이스 )`() {
            //given
            val maxInProgressSize = 3L
            val statusPTokenQueues = listOf(TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.P))
            val firstWaitingTokenQueue = TokenQueueEntity(tokenQueueId = 2, userId = 2L, status = TokenQueueStatus.W)
            val waitingTokenQueues = listOf(firstWaitingTokenQueue)
            val expectedInProgressSize =
                if(maxInProgressSize < statusPTokenQueues.size + waitingTokenQueues.size) maxInProgressSize
                else waitingTokenQueues.size.toLong()

            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(statusPTokenQueues)
            given(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W))
                .willReturn(firstWaitingTokenQueue)
            given(tokenQueueRepository.findAllByTokenQueueIdIn(listOf(2L, 3L))).willReturn(listOf(firstWaitingTokenQueue))
            given(tokenQueueRepository.saveAll(listOf(firstWaitingTokenQueue))).willReturn(listOf(firstWaitingTokenQueue))

            //when
            val updateWaitingAllTokenToInProgress =
                tokenQueueService.updateWaitingAllTokenToInProgress(maxInProgressSize)

            //then
            then(tokenQueueRepository).should().findAllByStatus(TokenQueueStatus.P)
            then(tokenQueueRepository).should().findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)
            then(tokenQueueRepository).should().findAllByTokenQueueIdIn(listOf(2L, 3L))
            then(tokenQueueRepository).should().saveAll(listOf(firstWaitingTokenQueue))

            assertEquals(expectedInProgressSize, updateWaitingAllTokenToInProgress.size.toLong())
        }

        @Test
        fun `성공 (Waiting이 많은 정상 케이스 )`() {
            //given
            val maxInProgressSize = 3L
            val statusPTokenQueues = listOf(TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.P))
            val firstWaitingTokenQueue = TokenQueueEntity(tokenQueueId = 2, userId = 2L, status = TokenQueueStatus.W)
            val waitingTokenQueues = listOf(
                firstWaitingTokenQueue,
                TokenQueueEntity(tokenQueueId = 3, userId = 3L, status = TokenQueueStatus.W)
            )
            val expectedInProgressSize =
                if(maxInProgressSize < statusPTokenQueues.size + waitingTokenQueues.size) maxInProgressSize
                else waitingTokenQueues.size.toLong()

            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(statusPTokenQueues)
            given(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W))
                .willReturn(firstWaitingTokenQueue)
            given(tokenQueueRepository.findAllByTokenQueueIdIn(listOf(2L, 3L))).willReturn(waitingTokenQueues)
            given(tokenQueueRepository.saveAll(waitingTokenQueues)).willReturn(waitingTokenQueues)

            //when
            val updateWaitingAllTokenToInProgress =
                tokenQueueService.updateWaitingAllTokenToInProgress(maxInProgressSize)

            //then
            then(tokenQueueRepository).should().findAllByStatus(TokenQueueStatus.P)
            then(tokenQueueRepository).should().findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)
            then(tokenQueueRepository).should().findAllByTokenQueueIdIn(listOf(2L, 3L))
            then(tokenQueueRepository).should().saveAll(waitingTokenQueues)

            assertEquals(expectedInProgressSize, updateWaitingAllTokenToInProgress.size.toLong())
        }

        @Test
        fun `성공 (Waiting이 없는 정상 케이스 )`() {
            //given
            val maxInProgressSize = 3L
            val statusPTokenQueues = listOf(TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.P))
            val waitingTokenQueues = mutableListOf<TokenQueueEntity>()
            val expectedInProgressSize =
                if(maxInProgressSize < statusPTokenQueues.size + waitingTokenQueues.size) maxInProgressSize
                else waitingTokenQueues.size.toLong()

            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(statusPTokenQueues)
            given(tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)).willReturn(null)

            //when
            val updateWaitingAllTokenToInProgress =
                tokenQueueService.updateWaitingAllTokenToInProgress(maxInProgressSize)

            //then
            then(tokenQueueRepository).should().findAllByStatus(TokenQueueStatus.P)
            then(tokenQueueRepository).should().findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)

            assertEquals(expectedInProgressSize, updateWaitingAllTokenToInProgress.size.toLong())
        }
    }


    @Nested
    @DisplayName("토큰 시간이 만료되었는지 확인 후 만료된 경우 업데이트")
    inner class CheckAndUpdateTokenExpiration {
        @Test
        fun `성공 (3개 중 2개가 만료된 정상 케이스)`() {
            //given
            val firstTokenQueue = TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(40))
            val secondTokenQueue = TokenQueueEntity(tokenQueueId = 2, userId = 2L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(31))
            val thirdTokenQueue = TokenQueueEntity(tokenQueueId = 3, userId = 3L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(10))

            val statusPTokenQueues = listOf(firstTokenQueue, secondTokenQueue, thirdTokenQueue)
            val expectedUpdatingStatusPTokenQueues = listOf(firstTokenQueue, secondTokenQueue)

            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(statusPTokenQueues)
            given(tokenQueueRepository.saveAll(any())).willReturn(expectedUpdatingStatusPTokenQueues)

            //when
            val updatedTokenQueues = tokenQueueService.checkAndUpdateTokenExpiration()

            //then
            then(tokenQueueRepository).should().saveAll(any())
            assertEquals(expectedUpdatingStatusPTokenQueues.size, updatedTokenQueues.size)
        }

        @Test
        fun `성공 (만료된 토큰큐가 없는 정상 케이스)`() {
            //given
            val firstTokenQueue = TokenQueueEntity(tokenQueueId = 1, userId = 1L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(40))
            val secondTokenQueue = TokenQueueEntity(tokenQueueId = 2, userId = 2L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(31))
            val thirdTokenQueue = TokenQueueEntity(tokenQueueId = 3, userId = 3L, status = TokenQueueStatus.P, updatedAt = LocalDateTime.now().minusMinutes(10))

            val statusPTokenQueues = listOf(firstTokenQueue, secondTokenQueue, thirdTokenQueue)
            val expectedUpdatingStatusPTokenQueues = mutableListOf<TokenQueueEntity>()

            given(tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)).willReturn(statusPTokenQueues)
            given(tokenQueueRepository.saveAll(any())).willReturn(expectedUpdatingStatusPTokenQueues)

            //when
            val updatedTokenQueues = tokenQueueService.checkAndUpdateTokenExpiration()

            //then
            then(tokenQueueRepository).should().saveAll(any())
            assertEquals(expectedUpdatingStatusPTokenQueues.size, updatedTokenQueues.size)
        }
    }

    @Nested
    @DisplayName("토큰 시간이 만료되었는지 확인 후 만료된 경우 업데이트")
    inner class ThrowExceptionIfStatusIsNotInProgressTest {

        @Test
        fun `성공 (토큰상태가 InProgress인 정상 케이스)`() {
            //given
            val tokenQueue = TokenQueueEntity(userId = 1L, status = TokenQueueStatus.P)

            //when
            tokenQueueService.throwExceptionIfStatusIsNotInProgress(tokenQueue)
        }

        @Test
        fun `실패 (토큰상태가 InProgress가 아닌 케이스)`() {
            //given
            val tokenQueue = TokenQueueEntity(userId = 1L, status = TokenQueueStatus.W)

            //when
            val exception = assertThrows<TokenStatusIsNotProgressException> {
                tokenQueueService.throwExceptionIfStatusIsNotInProgress(tokenQueue)
            }

            assertEquals("토큰 상태가 'InProgress'가 아닙니다.", exception.message)
        }
    }
}