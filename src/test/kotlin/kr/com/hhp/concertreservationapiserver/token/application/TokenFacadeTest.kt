package kr.com.hhp.concertreservationapiserver.token.application

import kr.com.hhp.concertreservationapiserver.token.domain.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.user.domain.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TokenFacadeTest {

    @Autowired
    private lateinit var tokenFacade: TokenFacade

    @Autowired
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `토큰 생성`() {
        //given
        val user = userRepository.save(UserEntity())

        //when
        val tokenQueue = tokenFacade.createToken(userId = user.userId!!)

        //then
        assertNotNull(tokenQueue)
        assertNotNull(tokenQueue.token)
    }

    @Test
    fun `토큰 정보 조회`() {
        //given
        val user = userRepository.save(UserEntity())
        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

        //when
        val tokenInfo = tokenFacade.getTokenInfo(token = tokenQueue.token)

        //then
        assertNotNull(tokenInfo)
        assertEquals(tokenQueue.userId, tokenInfo.userId)
        assertEquals(tokenQueue.status.toString(), tokenInfo.status)
    }
}