package kr.com.hhp.concertreservationapiserver.common.config

import kr.com.hhp.concertreservationapiserver.common.presentation.interceptor.TokenInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(private val tokenInterceptor: TokenInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenInterceptor)
            .addPathPatterns("/**") // 모든 경로에 대해 인터셉터 적용
    }
}