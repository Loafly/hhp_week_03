package kr.com.hhp.concertreservationapiserver.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val cachedRequest = ContentCachingRequestWrapper(request)
        val cachedResponse = ContentCachingResponseWrapper(response)

        filterChain.doFilter(cachedRequest, cachedResponse)

        logRequestDetails(cachedRequest)
        logResponseDetails(cachedResponse)

        // 원래의 응답을 response 로 보낼 수 있도록 처리
        cachedResponse.copyBodyToResponse()
    }

    private fun logRequestDetails(request: HttpServletRequest) {
        val headers = request.headerNames.asSequence().joinToString { "$it: ${request.getHeader(it)}" }
        logger.info("Request URL : ${request.method} ${request.requestURI} Headers: $headers")
    }

    private fun logResponseDetails(response: ContentCachingResponseWrapper) {
        logger.info("Response Status : ${response.status} Body : ${String(response.contentAsByteArray)}")
    }
}