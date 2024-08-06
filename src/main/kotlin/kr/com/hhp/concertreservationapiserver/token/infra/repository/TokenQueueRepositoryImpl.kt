package kr.com.hhp.concertreservationapiserver.token.infra.repository

import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.repository.redis.TokenQueueRedisRepository
import org.springframework.stereotype.Repository

@Repository
class TokenQueueRepositoryImpl(private val tokenQueueRedisRepository: TokenQueueRedisRepository): TokenQueueRepository {
    override fun addWaitingToken(userId: Long): String {
        return tokenQueueRedisRepository.addWaitingToken(userId)
    }

    override fun getWaitingPosition(token: String): Long {
        return tokenQueueRedisRepository.getWaitingPosition(token)
    }

    override fun getUserIdByToken(token: String): Long? {
        return tokenQueueRedisRepository.getUserIdByToken(token)
    }

    override fun activateTokens(n: Long): Long {
        return tokenQueueRedisRepository.activateTokens(n)
    }

    override fun isActiveToken(token: String): Boolean {
        return tokenQueueRedisRepository.isActiveToken(token)
    }

    override fun deleteActiveToken(token: String) {
        tokenQueueRedisRepository.deleteActiveToken(token)
    }

}