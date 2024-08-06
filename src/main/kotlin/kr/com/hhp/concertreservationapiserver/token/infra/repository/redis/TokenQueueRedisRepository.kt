package kr.com.hhp.concertreservationapiserver.token.infra.repository.redis

import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

@Repository
class TokenQueueRedisRepository(private val redisTemplate: RedisTemplate<String, String>) {

    private val waitingTokens: ZSetOperations<String, String> = redisTemplate.opsForZSet()
    private val activeTokens: SetOperations<String, String> = redisTemplate.opsForSet()
    private val tokenInfo: HashOperations<String, String, String> = redisTemplate.opsForHash()

    fun addWaitingToken(userId: Long): String {
        val token = UUID.randomUUID().toString()
        val currentTime = Instant.now().epochSecond.toDouble()
        waitingTokens.add(WAITING_TOKENS, token, currentTime)
        tokenInfo.put(TOKEN_INFO, token, userId.toString())
        redisTemplate.expire("$WAITING_TOKENS:$token", EXPIRY_SIX_HOUR_SECONDS, TimeUnit.SECONDS)
        redisTemplate.expire("$TOKEN_INFO:$token", EXPIRY_SIX_HOUR_SECONDS, TimeUnit.SECONDS)

        return token
    }

    fun getWaitingPosition(token: String): Long {
        return waitingTokens.rank(WAITING_TOKENS, token) ?: 0
    }

    fun getUserIdByToken(token: String): Long? {
        return tokenInfo.get(TOKEN_INFO, token)?.toLong()
    }

    fun activateTokens(n: Long): Long {
        val tokensToActivate = waitingTokens.range(WAITING_TOKENS, 0, n - 1)
        var activatedCount = 0L
        tokensToActivate?.forEach { token ->
            activeTokens.add(ACTIVE_TOKENS, token)
            waitingTokens.remove(WAITING_TOKENS, token)
            redisTemplate.expire("$ACTIVE_TOKENS:$token", EXPIRY_SEVEN_MINUTE_SECONDS, TimeUnit.SECONDS)
            activatedCount++
        }

        return activatedCount
    }

    fun isActiveToken(token: String): Boolean {
        return activeTokens.isMember(ACTIVE_TOKENS, token) ?: false
    }

    fun deleteActiveToken(token: String) {
        activeTokens.remove(ACTIVE_TOKENS, token)
        tokenInfo.delete(TOKEN_INFO, token)
    }

    companion object {
        private const val WAITING_TOKENS = "waiting_tokens"
        private const val ACTIVE_TOKENS = "active_tokens"
        private const val TOKEN_INFO = "token_info"
        private const val EXPIRY_SIX_HOUR_SECONDS = 60 * 60 * 6L // 6시간
        private const val EXPIRY_SEVEN_MINUTE_SECONDS = 60 * 7L // 7분
    }
}