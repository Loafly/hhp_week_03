package kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository

import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity


interface WalletRepository {
    fun findByWalletId(walletId: Long): WalletEntity?
    fun findByUserId(userId: Long): WalletEntity?
    fun save(walletEntity: WalletEntity): WalletEntity
}