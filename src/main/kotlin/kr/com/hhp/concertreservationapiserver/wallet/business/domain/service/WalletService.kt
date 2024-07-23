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
}