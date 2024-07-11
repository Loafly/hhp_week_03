package kr.com.hhp.concertreservationapiserver.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    private val API_NAME = "Concert Reservation API"
    private val API_VERSION = "v1.0.0"

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title(API_NAME)
                    .version(API_VERSION)
            )
    }
}