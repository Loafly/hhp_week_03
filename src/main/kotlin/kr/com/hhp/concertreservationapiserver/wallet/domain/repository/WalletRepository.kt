package kr.com.hhp.concertreservationapiserver.wallet.domain.repository

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity


interface WalletRepository {
    fun findByWalletId(walletId: Long): WalletEntity?
    fun findByUserId(userId: Long): WalletEntity?
    fun save(walletEntity: WalletEntity): WalletEntity
}