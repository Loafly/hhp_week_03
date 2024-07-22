package kr.com.hhp.concertreservationapiserver.token.presentation.controller

class TokenRequestDto {
    data class Post(
        val userId: Long
    )
}