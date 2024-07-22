package kr.com.hhp.concertreservationapiserver.wallet.infra.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "wallet")
class WalletEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var walletId: Long? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: Long,

    @Column(name = "balance", nullable = false)
    var balance: Int = 0,
) {
    fun updateBalance(amount: Int) {
        balance += amount
    }
}