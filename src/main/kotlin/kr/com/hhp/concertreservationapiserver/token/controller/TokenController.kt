package kr.com.hhp.concertreservationapiserver.token.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.com.hhp.concertreservationapiserver.common.ErrorResponse
import kr.com.hhp.concertreservationapiserver.token.application.exception.TokenNotFoundException
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "Token")
class TokenController {

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TokenDto.TokenResponse::class))]
        ),

        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "유저가 없는경우 (userId <= 0)", value = "{ \"message\" : \"유저가 존재하지 않습니다. userId : 0\"}")
                ]
            )]
        ),
    ])
    @PostMapping
    fun createToken(@RequestBody request: TokenDto.PostRequest): TokenDto.TokenResponse {

        if(request.userId <= 0) {
            throw UserNotFoundException("유저가 존재하지 않습니다. userId : ${request.userId}")
        }

        val token = UUID.randomUUID().toString()

        return TokenDto.TokenResponse(token)
    }

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TokenDto.TokenInfoResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우 (token == token)", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                ]
            )]
        ),
    ])
    @GetMapping
    fun getTokenInfo(@RequestHeader(name = "token") token: String): TokenDto.TokenInfoResponse {

        if(token == "token") {
            throw TokenNotFoundException("토큰이 존재하지 않습니다. token : $token")
        }

        return TokenDto.TokenInfoResponse(
            1L,
            "Waiting",
            1L
        )
    }
}