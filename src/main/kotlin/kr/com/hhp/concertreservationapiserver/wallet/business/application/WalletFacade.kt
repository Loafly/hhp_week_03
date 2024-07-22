package kr.com.hhp.concertreservationapiserver.wallet.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.user.business.domain.service.UserService
import kr.com.hhp.concertreservationapiserver.wallet.presentation.controller.WalletResponseDto
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletHistoryService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import org.springframework.transaction.annotation.Transactional

@Facade
class WalletFacade(
    private val walletService: WalletService,
    private val userService: UserService,
    private val walletHistoryService: WalletHistoryService
) {

    // 잔액 조회
    @Transactional(readOnly = true)
    fun getBalance(walletId: Long, userId: Long): WalletDto.Wallet {
        val wallet = walletService.getByWalletId(walletId)
        val user = userService.getByUserId(userId)
        walletService.throwExceptionIfMisMatchUserId(wallet, userId)

        return WalletDto.Wallet(
            walletId = walletId,
            userId = user.userId!!,
            balance = wallet.balance
        )
    }

    // 잔액 충전
    @Transactional
    fun charge(walletId: Long, userId: Long, amount: Int): WalletDto.Wallet {
        val user = userService.getByUserId(userId)
        val chargedWallet = walletService.charge(walletId = walletId, userId = user.userId!!, amount = amount)

        val balance = chargedWallet.balance
        walletHistoryService.create(walletId = walletId, balance = balance, amount = amount)

        return WalletDto.Wallet(
            walletId = walletId,
            userId = user.userId!!,
            balance = balance
        )
    }
}