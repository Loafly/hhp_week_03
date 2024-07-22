package kr.com.hhp.concertreservationapiserver.wallet.presentation.controller

class WalletRequestDto {
    data class BalancePatch (
        val userId: Long,
        val amount: Int,
    )
}