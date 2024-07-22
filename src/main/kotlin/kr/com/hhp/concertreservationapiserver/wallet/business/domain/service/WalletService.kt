package kr.com.hhp.concertreservationapiserver.wallet.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import org.springframework.stereotype.Service

@Service
class WalletService(private val walletRepository: WalletRepository) {

    fun getByWalletId(walletId: Long): WalletEntity {
        return walletRepository.findByWalletId(walletId)
            ?: throw CustomException(ErrorCode.WALLET_NOT_FOUND)
    }

    fun getByUserId(userId: Long): WalletEntity {
        return walletRepository.findByUserId(userId)
            ?: throw CustomException(ErrorCode.WALLET_NOT_FOUND)
    }

    fun charge(walletId: Long, userId: Long, amount:Int): WalletEntity {
        val wallet = getByWalletId(walletId)
        throwExceptionIfMisMatchUserId(wallet, userId)

        if(amount < 0) {
            throw CustomException(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT)
        }

        wallet.updateBalance(amount)
        return walletRepository.save(wallet)
    }

    fun throwExceptionIfMisMatchUserId(wallet: WalletEntity, userId: Long) {
        if(userId != wallet.userId) {
            throw CustomException(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH)
        }
    }

    fun useBalance(userId: Long, amount: Int): WalletEntity {
        val wallet = getByUserId(userId)
        wallet.updateBalance(-amount)
        return walletRepository.save(wallet)
    }
}