package kr.com.hhp.concertreservationapiserver.concert.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table
@Entity(name = "concert_detail")
class ConcertDetailEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertDetailId: Long? = null,

    @Column(name = "concert_id", nullable = false, unique = true)
    val concertId: Long,

    @Column(name = "total_seat_count", nullable = false)
    var totalSeatCount: Int = 50,

    @Column(name = "reaming_seat_count", nullable = false)
    var remainingSeatCount: Int = 50,

    @Column(name = "reservation_start_date_time", nullable = false)
    val reservationStartDateTime: LocalDateTime,

    @Column(name = "reservation_end_date_time", nullable = false)
    val reservationEndDateTime: LocalDateTime,

)