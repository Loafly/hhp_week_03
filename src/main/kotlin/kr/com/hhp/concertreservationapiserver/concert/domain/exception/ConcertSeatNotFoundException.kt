package kr.com.hhp.concertreservationapiserver.concert.domain.exception

class ConcertSeatNotFoundException(override val message: String?) : Exception(message) {
}