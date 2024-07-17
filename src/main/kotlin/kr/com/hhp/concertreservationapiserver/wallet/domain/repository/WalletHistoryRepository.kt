package kr.com.hhp.concertreservationapiserver.wallet.domain.repository

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletHistoryEntity
import org.springframework.stereotype.Repository

@Repository
interface WalletHistoryRepository {
    fun save(walletHistoryEntity: WalletHistoryEntity): WalletHistoryEntity
}