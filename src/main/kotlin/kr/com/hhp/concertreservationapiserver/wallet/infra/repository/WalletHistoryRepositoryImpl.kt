package kr.com.hhp.concertreservationapiserver.wallet.infra.repository

import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletHistoryRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletHistoryEntity
import kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa.WalletHistoryJpaRepository
import org.springframework.stereotype.Repository

@Repository
class WalletHistoryRepositoryImpl(private val walletHistoryJpaRepository: WalletHistoryJpaRepository):
    WalletHistoryRepository {
    override fun save(walletHistoryEntity: WalletHistoryEntity): WalletHistoryEntity {
        return walletHistoryJpaRepository.save(walletHistoryEntity)
    }
}