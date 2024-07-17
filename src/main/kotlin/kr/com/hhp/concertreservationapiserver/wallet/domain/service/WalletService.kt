package kr.com.hhp.concertreservationapiserver.wallet.domain.service

import kr.com.hhp.concertreservationapiserver.user.domain.exception.UserIdMisMatchException
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.WalletNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.springframework.stereotype.Service

@Service
class WalletService(private val walletRepository: WalletRepository) {

    fun getByWalletId(walletId: Long): WalletEntity {
        return walletRepository.findByWalletId(walletId)
            ?: throw WalletNotFoundException("Wallet이 존재하지 않습니다. walletId : $walletId")
    }

    fun getByUserId(userId: Long): WalletEntity {
        return walletRepository.findByUserId(userId)
            ?: throw WalletNotFoundException("Wallet이 존재하지 않습니다. userId : $userId")
    }

    fun charge(walletId: Long, userId: Long, amount:Int): WalletEntity {
        val wallet = getByWalletId(walletId)
        throwExceptionIfMisMatchUserId(wallet, userId)

        if(amount < 0) {
            throw InvalidChargeAmountException("충전 금액은 양수여야 합니다. amount : $amount")
        }

        wallet.updateBalance(amount)
        return walletRepository.save(wallet)
    }

    fun throwExceptionIfMisMatchUserId(wallet: WalletEntity, userId: Long) {
        if(userId != wallet.userId) {
            throw UserIdMisMatchException("유저Id가 일치하지 않습니다. userId : ${userId}, wallet.userId : ${wallet.userId}")
        }
    }

    fun useBalance(userId: Long, amount: Int): WalletEntity {
        val wallet = getByUserId(userId)
        wallet.updateBalance(-amount)
        return walletRepository.save(wallet)
    }
}