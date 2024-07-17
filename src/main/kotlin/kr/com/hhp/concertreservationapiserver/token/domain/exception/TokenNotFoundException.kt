package kr.com.hhp.concertreservationapiserver.token.domain.exception

class TokenNotFoundException(override val message: String) : Exception(message) {
}