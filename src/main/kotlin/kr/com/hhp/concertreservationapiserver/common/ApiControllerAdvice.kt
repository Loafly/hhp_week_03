package kr.com.hhp.concertreservationapiserver.common

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

data class ErrorResponse(val code: String, val message: String)

@RestControllerAdvice
class ApiControllerAdvice : ResponseEntityExceptionHandler() {


    @ExceptionHandler(CustomException::class)
    fun handleCustomException(exception: CustomException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(exception.code, exception.message)
        return ResponseEntity(errorResponse, exception.httpStatus)
    }

}