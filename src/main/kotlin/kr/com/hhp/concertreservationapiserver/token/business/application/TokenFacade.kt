package kr.com.hhp.concertreservationapiserver.token.business.application

import kr.com.hhp.concertreservationapiserver.common.annotation.Facade
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueStatus
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
        val token = tokenQueueService.createToken(user.userId!!)

        return TokenDto.TokenQueue(token)
    }

    // 토큰 조회
    @Transactional(readOnly = true)
    fun getTokenInfo(token: String): TokenDto.TokenInfo {
        val userId = tokenQueueService.getUserIdByToken(token)
        val rank = tokenQueueService.getRankByToken(token)
        val activeToken = tokenQueueService.isActiveToken(token)

        return TokenDto.TokenInfo(
            userId = userId,
            status = if(activeToken) TokenQueueStatus.P.toString() else TokenQueueStatus.W.toString(),
            remainingNumber = rank
        )
    }

    // 토큰 유효성 검사
    @Transactional(readOnly = true)
    fun verifyToken(token: String?) {
        // 토큰 유효성 검사 로직
        if(token == null) {
            throw CustomException(ErrorCode.TOKEN_IS_NULL)
        }

        tokenQueueService.getUserIdByToken(token)
    }

    @Transactional(readOnly = true)
    fun verifyTokenIsInProgress(token: String?) {
        // 토큰 유효성 검사 로직
        if(token == null) {
            throw CustomException(ErrorCode.TOKEN_IS_NULL)
        }

        tokenQueueService.getUserIdByToken(token)
        tokenQueueService.throwExceptionIfStatusIsNotInProgress(token)
    }
}