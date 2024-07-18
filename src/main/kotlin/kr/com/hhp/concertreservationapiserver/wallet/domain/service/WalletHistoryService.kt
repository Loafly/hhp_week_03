package kr.com.hhp.concertreservationapiserver.wallet.domain.service

import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletHistoryRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletHistoryEntity
import org.springframework.stereotype.Service

@Service
class WalletHistoryService(private val walletHistoryRepository: WalletHistoryRepository) {

    fun create(walletId: Long, balance: Int, amount: Int): WalletHistoryEntity {
        val walletHistoryEntity = WalletHistoryEntity(walletId = walletId, amount = amount, balance = balance)
        return walletHistoryRepository.save(walletHistoryEntity)
    }
}