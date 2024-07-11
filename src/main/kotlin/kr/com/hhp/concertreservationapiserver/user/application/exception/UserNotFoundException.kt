package kr.com.hhp.concertreservationapiserver.user.application.exception

class UserNotFoundException(override val message: String) : Exception(message) {
}