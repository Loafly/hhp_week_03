package kr.com.hhp.concertreservationapiserver.token.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "token_queue")
@EntityListeners(AuditingEntityListener::class)
class TokenQueueEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var tokenQueueId: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: TokenQueueStatus = TokenQueueStatus.W,

    @Column(name = "token")
    val token: String = UUID.randomUUID().toString(),

    @CreatedDate
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "fail_reason")
    var failReason: String? = null,
)