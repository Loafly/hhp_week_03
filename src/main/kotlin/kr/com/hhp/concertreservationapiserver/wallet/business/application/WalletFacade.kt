package kr.com.hhp.concertreservationapiserver.wallet.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.user.business.domain.service.UserService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletBalanceType
import kr.com.hhp.concertreservationapiserver.wallet.presentation.controller.WalletResponseDto
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import org.springframework.transaction.annotation.Transactional

@Facade
class WalletFacade(
    private val userService: UserService,
    private val walletService: WalletService
) {

    // 잔액 조회
    @Transactional(readOnly = true)
    fun getBalance(walletId: Long, userId: Long): WalletDto.Wallet {
        val user = userService.getByUserId(userId)
        val wallet = walletService.getByWalletIdAndUserId(walletId = walletId, userId = userId)

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
        val wallet = walletService.updateBalance(
            walletId = walletId, userId = userId, amount = amount, balanceType = WalletBalanceType.C
        )

        return WalletDto.Wallet(
            walletId = walletId,
            userId = user.userId!!,
            balance = wallet.balance
        )
    }
}