package kr.com.hhp.concertreservationapiserver.token.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueStatus
import org.springframework.stereotype.Service

@Service
class TokenQueueService (private val tokenQueueRepository: TokenQueueRepository) {

    fun createByUserId(userId: Long): TokenQueueEntity {
        val tokenQueue = TokenQueueEntity(userId = userId)
        return tokenQueueRepository.save(tokenQueue)
    }

    fun getByToken(token: String): TokenQueueEntity {
        return tokenQueueRepository.findByToken(token)
            ?: throw CustomException(ErrorCode.TOKEN_NOT_FOUND)
    }

    fun getNullAbleFirstWaitingTokenQueue(): TokenQueueEntity? {
        return tokenQueueRepository.findFirstByStatusOrderByTokenQueueId(TokenQueueStatus.W)
    }

    fun getAllByProgressTokenQueue(): List<TokenQueueEntity> {
        return tokenQueueRepository.findAllByStatus(TokenQueueStatus.P)
    }

    fun saveAll(tokenQueues: List<TokenQueueEntity>): List<TokenQueueEntity> {
        return tokenQueueRepository.saveAll(tokenQueues)
    }

    fun getAllByTokenQueueIds(tokenQueueIds: List<Long>): List<TokenQueueEntity> {
        return tokenQueueRepository.findAllByTokenQueueIdIn(tokenQueueIds)
    }

    fun calculateRemainingNumber(firstWaitingToken: TokenQueueEntity?, currentWaitingToken: TokenQueueEntity): Long {
        if(firstWaitingToken != null && currentWaitingToken.status == TokenQueueStatus.W) {
            return currentWaitingToken.tokenQueueId!! - firstWaitingToken.tokenQueueId!!
        }

        return 0L
    }

    // 대기중인 토큰을 가능한 만큼 InProgress로 업데이트 진행
    // 가능한 만큼 = 전체 InProgress maxInProgressSize 의 사이즈 중, 비어있는 공간의 숫자만큼 waiting에서 업데이트
    fun updateWaitingAllTokenToInProgress(maxInProgressSize: Long): List<TokenQueueEntity> {
        val progressTokenQueues = getAllByProgressTokenQueue()
        val currentProgressCount = progressTokenQueues.size

        // 현재 P인 상태가 Max_Size 보다 크거나 같은경우
        if (currentProgressCount >= maxInProgressSize) {
            return mutableListOf()
        }
        val firstWaitingTokenQueue = getNullAbleFirstWaitingTokenQueue() ?: return mutableListOf()

        // waiting -> in progress 로 업데이트 가능한 수
        val addableInProgressCount = maxInProgressSize - currentProgressCount
        val tokenQueuesToUpdate = getAllTokenToInProgressUpdate(firstWaitingTokenQueue, addableInProgressCount)

        tokenQueuesToUpdate.forEach { it.updateToInProgress() }

        return saveAll(tokenQueuesToUpdate)
    }

    // 실질적으로 InProgress로 업데이트 될 TokenQueue 조회
    private fun getAllTokenToInProgressUpdate(firstWaitingToken: TokenQueueEntity, addableCountToInProgressStatus: Long): List<TokenQueueEntity> {

        // ex... FirstIndex = 100, addable = 10, LastIndex = 109 (First + addable - 1)
        val firstWaitingTokenQueueId = firstWaitingToken.tokenQueueId
        val lastWaitingTokenQueueId = firstWaitingTokenQueueId!!.plus(addableCountToInProgressStatus).minus(1)

        // waiting -> InProgress 업데이트를 진행할 TokenQueueIds
        val tokenIdsToUpdate = (firstWaitingTokenQueueId..lastWaitingTokenQueueId).toList()

        // waiting -> InProgress 업데이트를 진행 할 TokenQueues
        return getAllByTokenQueueIds(tokenIdsToUpdate)
    }

    // 토큰 시간이 만료되었는지 확인 후, 만료된 경우 만료로 업데이트
    fun checkAndUpdateTokenExpiration(): List<TokenQueueEntity>  {
        val progressTokens = getAllByProgressTokenQueue()

        val expiringTokens = ArrayList<TokenQueueEntity>()

        for (progressToken in progressTokens) {
            if(progressToken.isExpired()) {
                progressToken.expireToken()
                expiringTokens.add(progressToken)
            }
        }

        return saveAll(expiringTokens)
    }

    fun throwExceptionIfStatusIsNotInProgress(tokenQueue: TokenQueueEntity){
        if(!tokenQueue.isStatusInProgress()) {
            throw CustomException(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS)
        }
    }
}
