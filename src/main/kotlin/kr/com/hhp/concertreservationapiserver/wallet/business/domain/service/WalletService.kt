package kr.com.hhp.concertreservationapiserver.wallet.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletBalanceType
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletHistoryEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletHistoryRepository
import org.springframework.stereotype.Service

@Service
class WalletService(private val walletRepository: WalletRepository,
                    private val walletHistoryRepository: WalletHistoryRepository) {

    fun getByUserId(userId: Long): WalletEntity {
        return walletRepository.findByUserId(userId)
            ?: throw CustomException(ErrorCode.WALLET_NOT_FOUND)
    }

    // 지갑 조회 (walletId, userId)
    fun getByWalletIdAndUserId(walletId: Long, userId: Long): WalletEntity {
        val wallet = walletRepository.findByWalletId(walletId) ?: throw CustomException(ErrorCode.WALLET_NOT_FOUND)
        wallet.throwExceptionIfMisMatchUserId(userId)

        return wallet
    }

    // 잔액 사용/충전
    fun updateBalance(walletId: Long, userId: Long, amount: Int, balanceType: WalletBalanceType): WalletEntity {
        if(amount < 0) {
            throw CustomException(ErrorCode.WALLET_INVALID_AMOUNT)
        }

        val wallet = walletRepository.findByWalletId(walletId) ?: throw CustomException(ErrorCode.WALLET_NOT_FOUND)
        wallet.throwExceptionIfMisMatchUserId(userId)
        wallet.updateBalance(amount, balanceType)

        walletHistoryRepository.save(
            WalletHistoryEntity(walletId = walletId, amount = amount, balance = wallet.balance, balanceType = balanceType)
        )

        return walletRepository.save(wallet)
    }
}