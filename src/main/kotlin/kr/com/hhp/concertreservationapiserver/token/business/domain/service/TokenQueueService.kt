package kr.com.hhp.concertreservationapiserver.token.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import org.springframework.stereotype.Service

@Service
class TokenQueueService (private val tokenQueueRepository: TokenQueueRepository) {

    fun updateWaitingAllTokenToInProgress(maxInProgressSize: Long): Long {
        return tokenQueueRepository.activateTokens(maxInProgressSize)
    }

    fun createToken(userId: Long): String {
        return tokenQueueRepository.addWaitingToken(userId)
    }

    fun getUserIdByToken(token: String): Long {
        return tokenQueueRepository.getUserIdByToken(token) ?: throw CustomException(ErrorCode.TOKEN_NOT_FOUND)
    }

    fun getRankByToken(token: String): Long {
        return tokenQueueRepository.getWaitingPosition(token)
    }

    fun isActiveToken(token: String): Boolean {
        return tokenQueueRepository.isActiveToken(token)
    }

    fun throwExceptionIfStatusIsNotInProgress(token: String){
        val activeToken = tokenQueueRepository.isActiveToken(token)
        if(!activeToken){
            throw CustomException(ErrorCode.TOKEN_STATUS_IS_NOT_PROGRESS)
        }
    }

    fun deleteActiveToken(token: String) {
        tokenQueueRepository.deleteActiveToken(token);
    }
}
