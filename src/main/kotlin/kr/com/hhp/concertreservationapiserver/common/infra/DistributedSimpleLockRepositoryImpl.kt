package kr.com.hhp.concertreservationapiserver.common.infra

import kr.com.hhp.concertreservationapiserver.common.domain.repository.DistributedLockRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class DistributedSimpleLockRepositoryImpl (private val redisTemplate: RedisTemplate<String, String>) :
    DistributedLockRepository {

    override fun lock(lockKey: String, lockValue: String, expiration: Long): Boolean {
        val success = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue)
        if (success == true) {
            redisTemplate.expire(lockKey, expiration, TimeUnit.MILLISECONDS)
            return true
        }
        return false
    }

    override fun unlock(lockKey: String, lockValue: String): Boolean {
        val script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
        """.trimIndent()
        val redisScript = DefaultRedisScript<Long>(script, Long::class.java)
        val result = redisTemplate.execute(redisScript, listOf(lockKey), lockValue)
        return result != null && result == 1L
    }
}