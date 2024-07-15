package kr.com.hhp.concertreservationapiserver.concert.controller

import java.time.LocalDateTime

class ConcertDto {

    data class DetailResponse (
        val concertId: Long,
        val totalSeatCount: Int,
        val remainingSeatCount: Int,
        val reservationStartDateTime: LocalDateTime,
        val reservationEndDateTime: LocalDateTime,
    )

    data class SeatResponse (
        val concertSeatId: Long,
        val seatNumber: Int,
        val price: Int,
        val reservationStatus: String
    )

    data class ReservationSeatRequest (
        val concertSeatId: Long,
    )

}