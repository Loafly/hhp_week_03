package kr.com.hhp.concertreservationapiserver.wallet.presentation.controller

class WalletDto {

    data class BalanceResponse (
        val walletId:Long,
        val userId:Long,
        val balance:Int
    )

    data class BalancePatchRequest (
        val userId: Long,
        val amount: Int,
    )
}