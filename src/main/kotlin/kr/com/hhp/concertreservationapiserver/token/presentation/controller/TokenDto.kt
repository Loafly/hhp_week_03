package kr.com.hhp.concertreservationapiserver.token.presentation.controller

class TokenDto {

    data class PostRequest(
        val userId: Long
    )

    data class TokenResponse(
        val token: String
    )

    data class TokenInfoResponse(
        val userId: Long,
        val status: String,
        val remainingNumber: Long
    )
}