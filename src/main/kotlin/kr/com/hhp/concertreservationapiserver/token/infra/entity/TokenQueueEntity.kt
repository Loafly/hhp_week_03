package kr.com.hhp.concertreservationapiserver.token.infra.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.com.hhp.concertreservationapiserver.token.domain.exception.TokenStatusIsNotProgressException
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

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
) {
    fun isExpired() :Boolean {
        if(status == TokenQueueStatus.C || status == TokenQueueStatus.F) {
            return true;
        }

        if(status == TokenQueueStatus.P) {
            val currentTime = LocalDateTime.now()
            // P로 업데이트된 시간이 30분이 지난 경우
            return currentTime.minusMinutes(30).isBefore(updatedAt)
        }

        return false
    }

    fun expireToken() {
        status = TokenQueueStatus.F
        failReason = "시간초과...."
    }

    fun updateToInProgress() {
        status = TokenQueueStatus.P
    }

    fun isStatusInProgress(): Boolean {
        return this.status == TokenQueueStatus.P
    }
}