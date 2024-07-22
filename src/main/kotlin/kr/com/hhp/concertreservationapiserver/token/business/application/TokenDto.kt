package kr.com.hhp.concertreservationapiserver.token.business.application

class TokenDto {
    data class TokenQueue(
        val token: String
    )

    data class TokenInfo(
        val userId: Long,
        val status: String,
        val remainingNumber: Long
    )
}