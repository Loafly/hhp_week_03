package kr.com.hhp.concertreservationapiserver.token.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.token.controller.TokenDto
import kr.com.hhp.concertreservationapiserver.user.application.UserService
import org.springframework.transaction.annotation.Transactional

@Facade
class TokenFacade(private val userService: UserService,
                  private val tokenQueueService: TokenQueueService) {

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
}