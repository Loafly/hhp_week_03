package kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity

enum class WalletBalanceType(private val description: String) {
    U("Use"),
    C("Charge")
}