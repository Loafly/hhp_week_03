package kr.com.hhp.concertreservationapiserver.concert.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Table
@Entity(name = "concert_reservation_history")
@EntityListeners(AuditingEntityListener::class)
class ConcertReservationHistoryEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertReservationHistoryId: Long? = null,

    @Column(name = "concert_seat_id", nullable = false)
    val concertSeatId: Long,

    @Column(name = "status", nullable = false)
    var status: ConcertReservationStatus,

    @CreatedDate
    @Column(name = "create_at", nullable = false)
    val createAt: LocalDateTime = LocalDateTime.now(),

    )