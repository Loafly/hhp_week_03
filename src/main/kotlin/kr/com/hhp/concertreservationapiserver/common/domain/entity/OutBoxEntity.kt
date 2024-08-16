package kr.com.hhp.concertreservationapiserver.common.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Table
@Entity(name = "out_box")
@EntityListeners(AuditingEntityListener::class)
class OutBoxEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var outBoxId: Long? = null,

    @Column(name = "event_type")
    val eventType: String,

    @Column(name = "payload")
    val payload: String,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var eventStatus: EventStatus,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun updateStatus(eventStatus: EventStatus) {
        this.eventStatus = eventStatus;
    }
}