package kr.com.hhp.concertreservationapiserver.wallet.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import org.springframework.stereotype.Service

@Service
class WalletBaseService(private val walletService: WalletService,
                        private val walletHistoryService: WalletHistoryService,) {

    // 지갑 조회 (walletId, userId)
    fun getByWalletIdAndUserId(walletId: Long, userId: Long): WalletEntity {
        val wallet = walletService.getByWalletId(walletId)
        wallet.throwExceptionIfMisMatchUserId(userId)

        return wallet
    }

    // 잔액 충전
    fun charge(walletId: Long, userId: Long, amount: Int): WalletEntity {

        if(amount < 0) {
            throw CustomException(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT)
        }

        val wallet = walletService.getByWalletId(walletId)
        wallet.throwExceptionIfMisMatchUserId(userId)
        wallet.updateBalance(amount)
        walletHistoryService.create(walletId = walletId, balance = wallet.balance, amount = amount)

        return wallet
    }
}