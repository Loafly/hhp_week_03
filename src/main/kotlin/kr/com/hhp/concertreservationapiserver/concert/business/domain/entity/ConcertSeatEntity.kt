package kr.com.hhp.concertreservationapiserver.concert.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Table
@Entity(name = "concert_seat")
@EntityListeners(AuditingEntityListener::class)
class ConcertSeatEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertSeatId: Long? = null,

    @Column(name = "user_id")
    var userId: Long? = null,

    @Column(name = "concert_detail_id", nullable = false)
    val concertDetailId: Long,

    @Column(name = "seat_number", nullable = false)
    val seatNumber: Int,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "reservation_status", nullable = false)
    var reservationStatus: ConcertReservationStatus = ConcertReservationStatus.A,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Version
    var version: Long = 0
) {
    fun updateReservationStatusC(userId: Long) {
        if(reservationStatus != ConcertReservationStatus.T) {
            throw CustomException(ErrorCode.CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS)
        }

        if(this.userId != userId) {
            throw CustomException(ErrorCode.CONCERT_USER_ID_IS_MIS_MATCH)
        }

        this.reservationStatus = ConcertReservationStatus.C
    }

    fun updateReservationStatusT(userId: Long) {
        if(reservationStatus != ConcertReservationStatus.A) {
            throw CustomException(ErrorCode.CONCERT_SEAT_ALREADY_RESERVED)
        }

        this.userId = userId
        this.reservationStatus = ConcertReservationStatus.T
    }

    fun expiredTemporaryReservation() {
        this.reservationStatus = ConcertReservationStatus.A
        this.userId = null
    }
}