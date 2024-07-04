package kr.com.hhp.concertreservationapiserver.common

import kr.com.hhp.concertreservationapiserver.token.application.exception.TokenNotFoundException
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserIdMisMatchException
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.application.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.application.exception.WalletNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

data class ErrorResponse(val message: String?)

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [
        InvalidChargeAmountException::class,
        UserIdMisMatchException::class
    ])
    fun handleCustomBadRequestExceptions(e: Exception): ResponseEntity<ErrorResponse> {

        return ResponseEntity(ErrorResponse(e.message), HttpStatus.BAD_REQUEST)
    }



    @ExceptionHandler(value = [
        WalletNotFoundException::class,
        UserNotFoundException::class,
        TokenNotFoundException::class
    ])
    fun handleCustomNotFoundExceptions(e: Exception): ResponseEntity<ErrorResponse> {

        return ResponseEntity(ErrorResponse(e.message), HttpStatus.NOT_FOUND)
    }

}