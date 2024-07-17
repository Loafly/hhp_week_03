package kr.com.hhp.concertreservationapiserver.wallet.domain.exception

class WalletNotFoundException(override val message: String): Exception(message) {
}