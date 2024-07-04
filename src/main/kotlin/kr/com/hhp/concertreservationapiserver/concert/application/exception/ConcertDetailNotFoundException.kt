package kr.com.hhp.concertreservationapiserver.concert.application.exception

class ConcertDetailNotFoundException(override val message: String) : Exception(message) {
}