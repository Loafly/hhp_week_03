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
        val concertSeatId:  Long,
        val seatNumber: Long,
        val price: Long,
        val reservationStatus: String
    )

    data class ReservationSeatRequest (
        val concertSeatId: Long,
        val userId: Long,
    )

}