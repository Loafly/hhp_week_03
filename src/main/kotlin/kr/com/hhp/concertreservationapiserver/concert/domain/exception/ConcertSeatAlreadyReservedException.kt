package kr.com.hhp.concertreservationapiserver.concert.domain.exception

class ConcertSeatAlreadyReservedException(override val message: String?) : Exception(message) {
}