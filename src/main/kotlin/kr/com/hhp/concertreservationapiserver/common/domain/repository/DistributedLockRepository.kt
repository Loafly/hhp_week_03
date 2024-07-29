package kr.com.hhp.concertreservationapiserver.common.domain.repository

interface DistributedLockRepository {
    fun lock(lockKey: String, lockValue: String, expiration: Long): Boolean
    fun unlock(lockKey: String, lockValue: String): Boolean
}