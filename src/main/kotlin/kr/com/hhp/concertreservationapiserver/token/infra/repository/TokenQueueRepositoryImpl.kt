package kr.com.hhp.concertreservationapiserver.token.infra.repository

import kr.com.hhp.concertreservationapiserver.token.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueStatus
import kr.com.hhp.concertreservationapiserver.token.infra.repository.jpa.TokenQueueJpaRepository
import org.springframework.stereotype.Repository

@Repository
class TokenQueueRepositoryImpl(private val tokenQueueJpaRepository: TokenQueueJpaRepository): TokenQueueRepository {
    override fun save(tokenQueue: TokenQueueEntity): TokenQueueEntity {
        return tokenQueueJpaRepository.save(tokenQueue)
    }

    override fun findByToken(token: String): TokenQueueEntity? {
        return tokenQueueJpaRepository.findByToken(token)
    }

    override fun findFirstByStatusOrderByTokenQueueId(tokenQueueStatus: TokenQueueStatus): TokenQueueEntity? {
        return tokenQueueJpaRepository.findFirstByStatusOrderByTokenQueueId(tokenQueueStatus)
    }

    override fun findAllByStatus(tokenStatus: TokenQueueStatus): List<TokenQueueEntity> {
        return tokenQueueJpaRepository.findAllByStatus(tokenStatus)
    }

    override fun saveAll(tokenQueues: List<TokenQueueEntity>): List<TokenQueueEntity> {
        return tokenQueueJpaRepository.saveAll(tokenQueues)
    }

    override fun findAllByTokenQueueIdIn(tokenQueueIds: List<Long>): List<TokenQueueEntity> {
        return tokenQueueJpaRepository.findAllByTokenQueueIdIn(tokenQueueIds)
    }
}