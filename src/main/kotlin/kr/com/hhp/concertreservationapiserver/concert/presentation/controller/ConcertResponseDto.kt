package kr.com.hhp.concertreservationapiserver.concert.presentation.controller

import kr.com.hhp.concertreservationapiserver.concert.business.application.ConcertDto
import java.time.LocalDateTime

class ConcertResponseDto {

    data class Detail(private val detail: ConcertDto.Detail) {
        val concertId: Long = detail.concertId
        val totalSeatCount: Int = detail.totalSeatCount
        val remainingSeatCount: Int = detail.remainingSeatCount
        val reservationStartDateTime: LocalDateTime = detail.reservationStartDateTime
        val reservationEndDateTime: LocalDateTime = detail.reservationEndDateTime
    }

    data class Seat (private val seat: ConcertDto.Seat) {
        val concertSeatId: Long = seat.concertSeatId
        val seatNumber: Int = seat.seatNumber
        val price: Int = seat.price
        val reservationStatus: String = seat.reservationStatus
    }
}