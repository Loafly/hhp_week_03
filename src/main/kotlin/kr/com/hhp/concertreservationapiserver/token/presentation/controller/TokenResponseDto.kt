package kr.com.hhp.concertreservationapiserver.token.presentation.controller

import kr.com.hhp.concertreservationapiserver.token.business.application.TokenDto

class TokenResponseDto {

    data class Token(private val tokenQueue: TokenDto.TokenQueue) {
        val token: String = tokenQueue.token
    }

    data class TokenInfo(private val tokenInfo: TokenDto.TokenInfo) {
        val userId: Long = tokenInfo.userId
        val status: String = tokenInfo.status
        val remainingNumber: Long = tokenInfo.remainingNumber
    }
}