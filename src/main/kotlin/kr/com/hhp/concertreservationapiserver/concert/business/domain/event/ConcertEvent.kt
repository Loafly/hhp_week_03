package kr.com.hhp.concertreservationapiserver.concert.business.domain.event

class ConcertEvent {

    data class Reservation(
        val concertSeatId: Long,
    )

    data class Pay(
        val concertSeatId: Long,
        val userId: Long,
        val price: Int,
        val walletId: Long,
        val token: String
    )

}
