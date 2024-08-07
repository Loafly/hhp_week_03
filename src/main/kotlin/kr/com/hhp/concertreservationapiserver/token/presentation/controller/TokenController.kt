package kr.com.hhp.concertreservationapiserver.token.presentation.controller

import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.com.hhp.concertreservationapiserver.common.ErrorResponse
import kr.com.hhp.concertreservationapiserver.common.annotation.RequiredToken
import kr.com.hhp.concertreservationapiserver.token.business.application.TokenFacade
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tokens")
@Tag(name = "Token")
class TokenController(private val tokenFacade: TokenFacade) {

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TokenResponseDto.Token::class))]
        ),

        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "유저가 없는경우", value = "{ \"message\" : \"유저가 존재하지 않습니다. userId : 0\"}")
                ]
            )]
        ),
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createToken(@RequestBody request: TokenRequestDto.Post): TokenResponseDto.Token {

        return TokenResponseDto.Token(tokenFacade.createToken(userId = request.userId))
    }

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = TokenResponseDto.TokenInfo::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰 값이 null인 경우", value = "{ \"message\" : \"토큰값이 null 입니다.\"}"),
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                ]
            )]
        ),
    ])
    @RequiredToken
    @GetMapping
    fun getTokenInfo(@RequestHeader(name = "token") token: String): TokenResponseDto.TokenInfo {

        return TokenResponseDto.TokenInfo(tokenFacade.getTokenInfo(token))
    }
}