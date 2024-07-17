package kr.com.hhp.concertreservationapiserver.concert.domain.exception

class ConcertNotFoundException(override val message: String) : Exception(message) {
}