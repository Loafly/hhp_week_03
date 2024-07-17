package kr.com.hhp.concertreservationapiserver.wallet.infra.repository

import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa.WalletJpaRepository
import org.springframework.stereotype.Repository

@Repository
class WalletRepositoryImpl(private val walletJpaRepository: WalletJpaRepository): WalletRepository {

    override fun findByWalletId(walletId: Long): WalletEntity? {
        return walletJpaRepository.findByWalletId(walletId)
    }

    override fun findByUserId(userId: Long): WalletEntity? {
        return walletJpaRepository.findByUserId(userId)
    }

    override fun save(walletEntity: WalletEntity): WalletEntity {
        return walletJpaRepository.save(walletEntity)
    }
}