package kr.com.hhp.concertreservationapiserver.token.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.token.controller.TokenDto
import kr.com.hhp.concertreservationapiserver.token.domain.exception.TokenIsNullException
import kr.com.hhp.concertreservationapiserver.token.domain.service.TokenQueueService
import kr.com.hhp.concertreservationapiserver.user.domain.service.UserService
import org.springframework.transaction.annotation.Transactional

@Facade
class TokenFacade(private val userService: UserService,
                  private val tokenQueueService: TokenQueueService
) {

    // 토큰 발급
    @Transactional
    fun createToken(userId: Long): TokenDto.TokenResponse {
        val user = userService.getByUserId(userId)
        val tokenQueue = tokenQueueService.createByUserId(user.userId!!)

        return TokenDto.TokenResponse(tokenQueue.token)
    }

    // 토큰 조회
    @Transactional(readOnly = true)
    fun getTokenInfo(token: String): TokenDto.TokenInfoResponse {
        val tokenQueue = tokenQueueService.getByToken(token)
        val firstWaitingToken = tokenQueueService.getNullAbleFirstWaitingTokenQueue()
        val remainingNumber = tokenQueueService.calculateRemainingNumber(firstWaitingToken, tokenQueue)

        return TokenDto.TokenInfoResponse(
            userId = tokenQueue.userId,
            status = tokenQueue.status.toString(),
            remainingNumber = remainingNumber
        )
    }

    // 토큰 유효성 검사
    @Transactional(readOnly = true)
    fun verifyToken(token: String?) {
        // 토큰 유효성 검사 로직
        if(token == null) {
            throw TokenIsNullException("Token 이 Null 입니다.")
        }

        tokenQueueService.getByToken(token)
    }

    @Transactional(readOnly = true)
    fun verifyTokenIsInProgress(token: String?) {
        // 토큰 유효성 검사 로직
        if(token == null) {
            throw TokenIsNullException("Token 이 Null 입니다.")
        }

        val tokenQueue = tokenQueueService.getByToken(token)
        tokenQueueService.throwExceptionIfStatusIsNotInProgress(tokenQueue)
    }
}