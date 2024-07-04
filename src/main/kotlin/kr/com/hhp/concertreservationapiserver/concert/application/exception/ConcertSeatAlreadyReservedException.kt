package kr.com.hhp.concertreservationapiserver.concert.application.exception

class ConcertSeatAlreadyReservedException(override val message: String?) : Exception(message) {
}