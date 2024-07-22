package kr.com.hhp.concertreservationapiserver.common.domain.exception

import org.springframework.http.HttpStatus

class CustomException(private val errorCode: ErrorCode) : RuntimeException(errorCode.message) {
    val code: String = errorCode.code
    val httpStatus: HttpStatus = errorCode.httpStatus
    override val message: String = errorCode.message
}