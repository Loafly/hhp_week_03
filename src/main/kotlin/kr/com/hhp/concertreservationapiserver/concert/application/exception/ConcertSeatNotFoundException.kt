package kr.com.hhp.concertreservationapiserver.concert.application.exception

class ConcertSeatNotFoundException(override val message: String?) : Exception(message) {
}