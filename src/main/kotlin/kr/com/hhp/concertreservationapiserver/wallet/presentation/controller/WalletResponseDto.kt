package kr.com.hhp.concertreservationapiserver.wallet.presentation.controller

import kr.com.hhp.concertreservationapiserver.wallet.business.application.WalletDto

class WalletResponseDto {

    data class Balance (private val wallet: WalletDto.Wallet) {
        val walletId:Long = wallet.walletId
        val userId:Long = wallet.userId
        val balance:Int = wallet.balance
    }

}