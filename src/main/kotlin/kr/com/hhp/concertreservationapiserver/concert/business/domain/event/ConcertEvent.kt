package kr.com.hhp.concertreservationapiserver.concert.business.domain.event

import kotlinx.serialization.Serializable


class ConcertEvent {

    @Serializable
    data class Reservation(
        val concertSeatId: Long,
    )

    @Serializable
    data class Pay(
        val concertSeatId: Long,
        val userId: Long,
        val price: Int,
        val walletId: Long,
        val token: String
    )

}
