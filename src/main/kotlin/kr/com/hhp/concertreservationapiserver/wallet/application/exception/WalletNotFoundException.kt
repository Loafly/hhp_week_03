package kr.com.hhp.concertreservationapiserver.wallet.application.exception

class WalletNotFoundException(override val message: String): Exception(message) {
}