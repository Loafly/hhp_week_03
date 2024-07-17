package kr.com.hhp.concertreservationapiserver.user.domain.exception

class UserNotFoundException(override val message: String) : Exception(message) {
}