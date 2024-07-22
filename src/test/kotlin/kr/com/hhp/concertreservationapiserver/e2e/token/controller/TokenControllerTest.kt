package kr.com.hhp.concertreservationapiserver.e2e.token.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TokenControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenQueueRepository: TokenQueueRepository

    @Nested
    @DisplayName("토큰 생성 조회")
    inner class CreateTokenTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to user.userId))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/tokens")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.token").isString)
        }

        @Test
        fun `실패 (유저가 존재하지 않는 경우)`() {
            //given
            val userId = 1

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to userId))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/tokens")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.code))
        }
    }


    @Nested
    @DisplayName("콘서트 상세 조회")
    inner class GetTokenTokenInfo {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tokens")
                    .header("token", tokenQueue.token)
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.userId").value(tokenQueue.userId))
                .andExpect(jsonPath("$.status").value(tokenQueue.status.toString()))
        }

        @Test
        fun `실패 (토큰이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!))
            val invalidToken = tokenQueue.token + "-notFound"

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tokens")
                    .header("token", invalidToken)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_NOT_FOUND.code))
        }

        @Test
        fun `실패 (토큰이 null 경우)`() {
            //given

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/tokens")
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_IS_NULL.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.TOKEN_IS_NULL.code))
        }
    }


}