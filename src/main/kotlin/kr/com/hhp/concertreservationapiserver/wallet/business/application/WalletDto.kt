package kr.com.hhp.concertreservationapiserver.wallet.business.application

class WalletDto {

    data class Wallet (
        val walletId:Long,
        val userId:Long,
        val balance:Int
    )
}