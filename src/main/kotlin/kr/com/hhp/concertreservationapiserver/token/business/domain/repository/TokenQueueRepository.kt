package kr.com.hhp.concertreservationapiserver.token.business.domain.repository

import org.springframework.stereotype.Repository

@Repository
interface TokenQueueRepository{
    fun addWaitingToken(userId: Long): String
    fun getWaitingPosition(token: String): Long
    fun getUserIdByToken(token: String): Long?
    fun activateTokens(n: Long): Long
    fun isActiveToken(token: String): Boolean
    fun deleteActiveToken(token: String)
}