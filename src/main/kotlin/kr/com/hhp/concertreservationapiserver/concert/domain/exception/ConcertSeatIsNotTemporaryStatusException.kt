package kr.com.hhp.concertreservationapiserver.concert.domain.exception

class ConcertSeatIsNotTemporaryStatusException(override val message: String?) : Exception(message) {
}