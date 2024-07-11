package kr.com.hhp.concertreservationapiserver.wallet.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.com.hhp.concertreservationapiserver.common.ErrorResponse
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserIdMisMatchException
import kr.com.hhp.concertreservationapiserver.user.application.exception.UserNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.application.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.application.exception.WalletNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/wallets")
@Tag(name = "Wallet")
class WalletController {

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = WalletDto.BalanceResponse::class))]
        ),

        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "wallet 의 userId와 일치하지 않는 경우 (walletId != userId)", value = "{ \"message\" : \"유저Id가 일치하지 않습니다. userId : 1, wallet.userId : 2\"}"),
                ]
            )]
        ),

        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "지갑이 없는경우 (walletId <= 0)", value = "{ \"message\" : \"지갑이 존재하지 않습니다. walletId : 0\"}"),
                    ExampleObject(name = "유저가 없는경우 (userId <= 0)", value = "{ \"message\" : \"유저가 존재하지 않습니다. userId : 0\"}")
                ]
            )]
        ),
    ])
    @GetMapping("/{walletId}/balance")
    fun getBalance(
        @PathVariable walletId: Long,
        @RequestParam userId: Long
    ) : WalletDto.BalanceResponse {
        if(walletId <= 0) {
            throw WalletNotFoundException("지갑이 존재하지 않습니다. walletId : $walletId")
        }

        if(userId <= 0) {
            throw UserNotFoundException("유저가 존재하지 않습니다. userId : $userId")
        }

        return WalletDto.BalanceResponse(
            walletId, userId, 1000L
        )
    }

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = WalletDto.BalanceResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "wallet 의 userId와 일치하지 않는 경우 (walletId != userId)", value = "{ \"message\" : \"유저Id가 일치하지 않습니다. userId : 1, wallet.userId : 2\"}"),
                    ExampleObject(name = "충전 금액이 음수인 경우 (amount < 0)", value = "{ \"message\" : \"충전 금액은 양수여야 합니다. amount : -1000\"}"),
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "지갑이 없는경우 (walletId <= 0)", value = "{ \"message\" : \"지갑이 존재하지 않습니다. walletId : 0\"}"),
                    ExampleObject(name = "유저가 없는경우 (userId <= 0)", value = "{ \"message\" : \"유저가 존재하지 않습니다. userId : 0\"}")
                ]
            )]
        ),
    ])
    @PatchMapping("/{walletId}/charge")
    fun updateBalance(
        @PathVariable walletId: Long, @RequestBody request: WalletDto.BalancePatchRequest
    ): WalletDto.BalanceResponse {
        if(walletId <= 0) {
            throw WalletNotFoundException("지갑이 존재하지 않습니다. walletId : $walletId")
        }

        if(request.userId <= 0) {
            throw UserNotFoundException("유저가 존재하지 않습니다. userId : ${request.userId}")
        }

        if(walletId != request.userId) {
            throw UserIdMisMatchException("유저Id가 일치하지 않습니다. userId : ${request.userId}, wallet.userId : $walletId")
        }

        if(request.amount < 0) {
            throw InvalidChargeAmountException("충전 금액은 양수여야 합니다. amount : ${request.amount}")
        }

        return WalletDto.BalanceResponse(
            walletId, request.userId, request.amount
        )
    }

}