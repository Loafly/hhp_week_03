package kr.com.hhp.concertreservationapiserver.token.domain

import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueStatus
import org.springframework.stereotype.Repository

@Repository
interface TokenQueueRepository{

    fun save(tokenQueue: TokenQueueEntity): TokenQueueEntity

    fun findByToken(token: String): TokenQueueEntity?

    fun findFirstByStatusOrderByTokenQueueId(tokenQueueStatus: TokenQueueStatus): TokenQueueEntity?

    fun findAllByStatus(tokenStatus: TokenQueueStatus): List<TokenQueueEntity>

    fun saveAll(tokenQueues: List<TokenQueueEntity>): List<TokenQueueEntity>

    fun findAllByTokenQueueIdIn(tokenQueueIds: List<Long>): List<TokenQueueEntity>
}