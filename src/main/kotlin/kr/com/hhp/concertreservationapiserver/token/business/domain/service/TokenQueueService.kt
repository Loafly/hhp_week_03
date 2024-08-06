package kr.com.hhp.concertreservationapiserver.token.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.infra.repository.redis.TokenQueueRedisRepository
import org.springframework.stereotype.Service

@Service
class TokenQueueService (private val tokenQueueRedisRepository: TokenQueueRedisRepository) {

    fun updateWaitingAllTokenToInProgress(maxInProgressSize: Long): Long {
        return tokenQueueRedisRepository.activateTokens(maxInProgressSize)
    }

    fun createToken(userId: Long): String {
        return tokenQueueRedisRepository.addWaitingToken(userId)
    }

    fun getUserIdByToken(token: String): Long {
        return tokenQueueRedisRepository.getUserIdByToken(token) ?: throw CustomException(ErrorCode.TOKEN_NOT_FOUND)
    }

    fun getRankByToken(token: String): Long {
        return tokenQueueRedisRepository.getWaitingPosition(token)
    }

    fun isActiveToken(token: String): Boolean {
        return tokenQueueRedisRepository.isActiveToken(token)
    }

    fun throwExceptionIfStatusIsNotInProgress(token: String){
        val activeToken = tokenQueueRedisRepository.isActiveToken(token)
        if(!activeToken){
            throw CustomException(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS)
        }
    }

    fun deleteActiveToken(token: String) {
        tokenQueueRedisRepository.deleteActiveToken(token);
    }
}
