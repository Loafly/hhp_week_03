package kr.com.hhp.concertreservationapiserver.wallet.domain

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.springframework.stereotype.Repository


interface WalletRepository {
    fun findByWalletId(walletId: Long): WalletEntity?
    fun findByUserId(userId: Long): WalletEntity?
    fun save(walletEntity: WalletEntity): WalletEntity
}