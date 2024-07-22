package kr.com.hhp.concertreservationapiserver.concert.business.application

import java.time.LocalDateTime

class ConcertDto {

    data class Detail (
        val concertId: Long,
        val totalSeatCount: Int,
        val remainingSeatCount: Int,
        val reservationStartDateTime: LocalDateTime,
        val reservationEndDateTime: LocalDateTime,
    )

    data class Seat (
        val concertSeatId: Long,
        val seatNumber: Int,
        val price: Int,
        val reservationStatus: String
    )
}