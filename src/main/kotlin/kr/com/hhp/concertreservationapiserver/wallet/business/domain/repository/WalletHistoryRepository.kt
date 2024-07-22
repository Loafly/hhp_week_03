package kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository

import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletHistoryEntity
import org.springframework.stereotype.Repository

@Repository
interface WalletHistoryRepository {
    fun save(walletHistoryEntity: WalletHistoryEntity): WalletHistoryEntity
}