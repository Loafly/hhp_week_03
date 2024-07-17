package kr.com.hhp.concertreservationapiserver.concert.domain.exception

class ConcertDetailNotFoundException(override val message: String) : Exception(message) {
}