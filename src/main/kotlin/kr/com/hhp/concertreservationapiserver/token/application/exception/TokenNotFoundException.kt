package kr.com.hhp.concertreservationapiserver.token.application.exception

class TokenNotFoundException(override val message: String) : Exception(message) {
}