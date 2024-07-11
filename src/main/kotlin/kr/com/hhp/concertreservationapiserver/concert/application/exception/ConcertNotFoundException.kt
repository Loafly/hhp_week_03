package kr.com.hhp.concertreservationapiserver.concert.application.exception

class ConcertNotFoundException(override val message: String) : Exception(message) {
}