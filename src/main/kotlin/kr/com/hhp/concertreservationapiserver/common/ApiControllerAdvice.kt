package kr.com.hhp.concertreservationapiserver.common

import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertDetailNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertReservationPeriodException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertSeatAlreadyReservedException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertSeatIsNotTemporaryStatusException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertSeatNotFoundException
import kr.com.hhp.concertreservationapiserver.token.domain.exception.TokenIsNullException
import kr.com.hhp.concertreservationapiserver.token.domain.exception.TokenNotFoundException
import kr.com.hhp.concertreservationapiserver.token.domain.exception.TokenStatusIsNotProgressException
import kr.com.hhp.concertreservationapiserver.user.domain.exception.UserIdMisMatchException
import kr.com.hhp.concertreservationapiserver.user.domain.exception.UserNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.WalletNotFoundException
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
        UserIdMisMatchException::class,
        TokenStatusIsNotProgressException::class,
        ConcertReservationPeriodException::class,
        ConcertSeatAlreadyReservedException::class,
        ConcertSeatIsNotTemporaryStatusException::class,
        TokenIsNullException::class
    ])
    fun handleCustomBadRequestExceptions(e: Exception): ResponseEntity<ErrorResponse> {

        return ResponseEntity(ErrorResponse(e.message), HttpStatus.BAD_REQUEST)
    }



    @ExceptionHandler(value = [
        WalletNotFoundException::class,
        UserNotFoundException::class,
        TokenNotFoundException::class,
        ConcertNotFoundException::class,
        ConcertDetailNotFoundException::class,
        ConcertSeatNotFoundException::class
    ])
    fun handleCustomNotFoundExceptions(e: Exception): ResponseEntity<ErrorResponse> {

        return ResponseEntity(ErrorResponse(e.message), HttpStatus.NOT_FOUND)
    }

}