package kr.com.hhp.concertreservationapiserver.concert.infra.entity

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
@Entity(name = "concert_seat_payment_history")
@EntityListeners(AuditingEntityListener::class)
class ConcertSeatPaymentHistoryEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertSeatPaymentHistoryId: Long? = null,

    @Column(name = "wallet_id", nullable = false)
    val walletId: Long,

    @Column(name = "concert_seat_id", nullable = false)
    val concertSeatId: Long,

    @Column(name = "price", nullable = false)
    val price: Int,

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

)