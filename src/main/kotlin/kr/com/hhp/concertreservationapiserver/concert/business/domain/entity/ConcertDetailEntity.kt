package kr.com.hhp.concertreservationapiserver.concert.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import java.time.LocalDateTime

@Table
@Entity(name = "concert_detail")
class ConcertDetailEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertDetailId: Long? = null,

    @Column(name = "concert_id", nullable = false)
    val concertId: Long,

    @Column(name = "total_seat_count", nullable = false)
    var totalSeatCount: Int = 50,

    @Column(name = "reaming_seat_count", nullable = false)
    var remainingSeatCount: Int = 50,

    @Column(name = "reservation_start_date_time", nullable = false)
    val reservationStartDateTime: LocalDateTime,

    @Column(name = "reservation_end_date_time", nullable = false)
    val reservationEndDateTime: LocalDateTime,

) {

    fun reserveSeat() {
        remainingSeatCount--
    }

    fun releaseSeat(count: Int) {
        remainingSeatCount += count
    }

    fun throwExceptionIfNotReservationPeriod() {
        // 예약 기간이 시작되지 않은 경우
        if(reservationStartDateTime.isAfter(LocalDateTime.now())) {
            throw CustomException(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY)
        }

        // 예약 기간이 끝난 경우
        if(reservationEndDateTime.isBefore(LocalDateTime.now())) {
            throw CustomException(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE)
        }
    }
}