package kr.com.hhp.concertreservationapiserver.wallet.application.exception

class InvalidChargeAmountException(override val message: String?) : Exception(message) {
}