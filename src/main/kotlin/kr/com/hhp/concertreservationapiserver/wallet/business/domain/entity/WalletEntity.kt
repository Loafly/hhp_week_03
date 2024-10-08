package kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.application.WalletDto

@Table
@Entity(name = "wallet")
class WalletEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var walletId: Long? = null,

    @Column(name = "user_id", nullable = false, unique = true)
    var userId: Long,

    @Column(name = "balance", nullable = false)
    var balance: Int = 0,
) {
    fun updateBalance(amount: Int, balanceType: WalletBalanceType) {

        if(balanceType == WalletBalanceType.U) {
            balance -= amount
        } else if (balanceType == WalletBalanceType.C) {
            balance += amount
        }

        if(balance < 0) {
            throw CustomException(ErrorCode.WALLET_INVALID_BALANCE)
        }
    }

    fun throwExceptionIfMisMatchUserId(userId: Long) {
        if(this.userId != userId) {
            throw CustomException(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH)
        }
    }
}