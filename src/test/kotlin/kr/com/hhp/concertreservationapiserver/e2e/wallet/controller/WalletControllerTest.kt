package kr.com.hhp.concertreservationapiserver.e2e.wallet.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.user.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.apache.catalina.User
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

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Nested
    @DisplayName("지갑 잔액 조회")
    inner class GetBalanceTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/wallets/${wallet.walletId}/balance")
                    .param("userId", user.userId.toString())
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.walletId").value(wallet.walletId))
                .andExpect(jsonPath("$.userId").value(wallet.userId))
                .andExpect(jsonPath("$.balance").value(wallet.balance))
        }

        @Test
        fun `실패 (유저가 존재하지 않는 경우)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val invalidUserId = 100
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/wallets/${wallet.walletId}/balance")
                    .param("userId", invalidUserId.toString())
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.code))
        }

        @Test
        fun `실패 (지갑이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val walletId = 1

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/wallets/${walletId}/balance")
                    .param("userId", user.userId.toString())
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.WALLET_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.WALLET_NOT_FOUND.code))
        }

        @Test
        fun `실패 (지갑 주인이 아닌 경우)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val walletUser = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = walletUser.userId!!, balance = balance))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/wallets/${wallet.walletId}/balance")
                    .param("userId", user.userId.toString())
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code))
        }
    }


    @Nested
    @DisplayName("지갑 잔액 충전")
    inner class UpdateBalanceTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))
            val amount = 50000

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to user.userId, "amount" to amount))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/wallets/${wallet.walletId}/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.walletId").value(wallet.walletId))
                .andExpect(jsonPath("$.userId").value(wallet.userId))
                .andExpect(jsonPath("$.balance").value(balance + amount))
        }

        @Test
        fun `실패 (유저가 존재하지 않는 경우)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val invalidUserId = 100
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))
            val amount = 50000

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to invalidUserId, "amount" to amount))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/wallets/${wallet.walletId}/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.code))
        }

        @Test
        fun `실패 (지갑이 존재하지 않는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val walletId = 1
            val amount = 50000

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to user.userId, "amount" to amount))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/wallets/${walletId}/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.message").value(ErrorCode.WALLET_NOT_FOUND.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.WALLET_NOT_FOUND.code))
        }

        @Test
        fun `실패 (지갑 주인이 아닌 경우)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val walletUser = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = walletUser.userId!!, balance = balance))
            val amount = 50000

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to user.userId, "amount" to amount))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/wallets/${wallet.walletId}/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code))
        }

        @Test
        fun `실패 (충전 금액이 음수인 경우)`() {
            //given
            val balance = 10000
            val user = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))
            val amount = -5000

            val requestBody = objectMapper.writeValueAsString(mapOf("userId" to user.userId, "amount" to amount))

            //when
            val perform = mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/wallets/${wallet.walletId}/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )

            //then
            perform
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT.message))
                .andExpect(jsonPath("$.code").value(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT.code))
        }
    }
}