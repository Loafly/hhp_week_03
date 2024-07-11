package kr.com.hhp.concertreservationapiserver.wallet.application

import kr.com.hhp.concertreservationapiserver.wallet.application.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.application.exception.WalletNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.domain.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.springframework.stereotype.Service

@Service
class WalletService(private val walletRepository: WalletRepository) {

    fun getByWalletId(walletId: Long): WalletEntity {
        return walletRepository.findByWalletId(walletId)
            ?: throw WalletNotFoundException("Wallet이 존재하지 않습니다. walletId : $walletId")
    }

    fun charge(walletId: Long, userId: Long, amount:Int): WalletEntity {
        val wallet = getByWalletId(walletId)
        wallet.throwExceptionIfMisMatchUserId(userId)

        if(amount < 0) {
            throw InvalidChargeAmountException("충전 금액은 양수여야 합니다. amount : $amount")
        }

        wallet.updateBalance(amount)
        return walletRepository.save(wallet)
    }
}