package kr.com.hhp.concertreservationapiserver.concert.application.exception

class ConcertSeatIsNotTemporaryStatusException(override val message: String?) : Exception(message) {
}