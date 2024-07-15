package kr.com.hhp.concertreservationapiserver.wallet.domain

import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletHistoryEntity
import org.springframework.stereotype.Repository

@Repository
interface WalletHistoryRepository {
    fun save(walletHistoryEntity: WalletHistoryEntity): WalletHistoryEntity
}