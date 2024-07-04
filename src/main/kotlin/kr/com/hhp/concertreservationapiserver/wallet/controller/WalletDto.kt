package kr.com.hhp.concertreservationapiserver.wallet.controller

class WalletDto {

    data class BalanceResponse (
        val walletId:Long,
        val userId:Long,
        val balance:Long
    )

    data class BalancePatchRequest (
        val userId:Long,
        val amount: Long,
    )
}