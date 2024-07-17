package kr.com.hhp.concertreservationapiserver.common.presentation.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.com.hhp.concertreservationapiserver.common.annotation.RequiredInProgressToken
import kr.com.hhp.concertreservationapiserver.common.annotation.RequiredToken
import kr.com.hhp.concertreservationapiserver.token.application.TokenFacade
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class TokenInterceptor(private val tokenFacade: TokenFacade): HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        if (handler is HandlerMethod) {
            val method = handler.method

            if (method.isAnnotationPresent(RequiredToken::class.java)) {
                val token = request.getHeader("token")
                tokenFacade.verifyToken(token)
            }

            if (method.isAnnotationPresent(RequiredInProgressToken::class.java)) {
                val token = request.getHeader("token")
                tokenFacade.verifyTokenIsInProgress(token)
            }
        }

        return true
    }
}