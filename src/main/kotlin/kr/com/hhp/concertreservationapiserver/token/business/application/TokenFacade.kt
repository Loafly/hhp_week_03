package kr.com.hhp.concertreservationapiserver.token.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import kr.com.hhp.concertreservationapiserver.user.business.domain.service.UserService
import org.springframework.transaction.annotation.Transactional

@Facade
class TokenFacade(private val userService: UserService,
                  private val tokenQueueService: TokenQueueService
) {

    // 토큰 발급
    @Transactional
    fun createToken(userId: Long): TokenDto.TokenQueue {
        val user = userService.getByUserId(userId)
        val tokenQueue = tokenQueueService.createByUserId(user.userId!!)

        return TokenDto.TokenQueue(tokenQueue.token)
    }

    // 토큰 조회
    @Transactional(readOnly = true)
    fun getTokenInfo(token: String): TokenDto.TokenInfo {
        val tokenQueue = tokenQueueService.getByToken(token)
        val firstWaitingToken = tokenQueueService.getNullAbleFirstWaitingTokenQueue()
        val remainingNumber = tokenQueueService.calculateRemainingNumber(firstWaitingToken, tokenQueue)

        return TokenDto.TokenInfo(
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
            throw CustomException(ErrorCode.TOKEN_IS_NULL)
        }

        tokenQueueService.getByToken(token)
    }

    @Transactional(readOnly = true)
    fun verifyTokenIsInProgress(token: String?) {
        // 토큰 유효성 검사 로직
        if(token == null) {
            throw CustomException(ErrorCode.TOKEN_IS_NULL)
        }

        val tokenQueue = tokenQueueService.getByToken(token)
        tokenQueueService.throwExceptionIfStatusIsNotInProgress(tokenQueue)
    }
}