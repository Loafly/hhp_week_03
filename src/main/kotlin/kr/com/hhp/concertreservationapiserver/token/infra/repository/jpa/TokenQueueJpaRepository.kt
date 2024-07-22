package kr.com.hhp.concertreservationapiserver.token.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueStatus
import org.springframework.data.jpa.repository.JpaRepository

interface TokenQueueJpaRepository:JpaRepository<TokenQueueEntity, Long> {

    fun findByToken(token: String): TokenQueueEntity?

    fun findFirstByStatusOrderByTokenQueueId(status: TokenQueueStatus): TokenQueueEntity?

    fun findAllByStatus(status: TokenQueueStatus): List<TokenQueueEntity>

    fun findAllByTokenQueueIdIn(tokenQueueIds: List<Long>): List<TokenQueueEntity>
}