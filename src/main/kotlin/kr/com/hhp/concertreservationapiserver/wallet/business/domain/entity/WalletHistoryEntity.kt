package kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity

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


@Entity
@Table(name = "wallet_history")
@EntityListeners(AuditingEntityListener::class)
class WalletHistoryEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var walletHistoryId: Long? = null,

    @Column(name = "wallet_id", nullable = false)
    var walletId: Long,

    @Column(name = "amount", nullable = false)
    var amount: Int,

    @Column(name = "balance", nullable = false)
    var balance: Int,

    @CreatedDate
    @Column(name = "created_at", nullable = false, columnDefinition = "timestamp default current_timestamp")
    var createdAt: LocalDateTime = LocalDateTime.now(),
)