package kr.com.hhp.concertreservationapiserver.integration.wallet.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.application.WalletFacade
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class WalletFacadeTest {

    @Autowired
    private lateinit var walletFacade: WalletFacade

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
            val user = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))

            //when
            val balanceResponse = walletFacade.getBalance(
                walletId = wallet.walletId!!,
                userId = user.userId!!,
            )

            //then
            assertNotNull(balanceResponse)
            assertEquals(wallet.userId, balanceResponse.userId)
            assertEquals(wallet.walletId, balanceResponse.walletId)
            assertEquals(wallet.balance, balanceResponse.balance)
        }

        @Test
        fun `실패 (지갑이 없는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val walletId = 1L

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.getBalance(
                    walletId = walletId,
                    userId = user.userId!!,
                )
            }

            //then
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (사용자가 없는 경우)`() {
            //given
            val userId = 1L
            val wallet = walletRepository.save(WalletEntity(userId = userId))

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.getBalance(
                    walletId = wallet.walletId!!,
                    userId = userId,
                )
            }

            //then
            assertEquals(ErrorCode.USER_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.USER_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (지갑 주인이 아닌 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val walletUser = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = walletUser.userId!!))

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.getBalance(
                    walletId = wallet.walletId!!,
                    userId = user.userId!!,
                )
            }

            //then
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code, exception.code)
        }
    }


    @Nested
    @DisplayName("지갑 잔액 충전")
    inner class ChargeTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val user = userRepository.save(UserEntity())
            val balance = 10000
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))
            val amount = 1000

            //when
            val balanceResponse = walletFacade.charge(
                walletId = wallet.walletId!!,
                userId = user.userId!!,
                amount
            )

            //then
            assertNotNull(balanceResponse)
            assertEquals(wallet.userId, balanceResponse.userId)
            assertEquals(wallet.walletId, balanceResponse.walletId)
            assertEquals(balance + amount, balanceResponse.balance)
        }

        @Test
        fun `실패 (유저가 존재하지 않는 경우)`() {
            //given
            val userId = 1L
            val balance = 10000
            val wallet = walletRepository.save(WalletEntity(userId = userId, balance = balance))
            val amount = 1000

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.charge(
                    walletId = wallet.walletId!!,
                    userId = userId,
                    amount
                )
            }

            //then
            assertEquals(ErrorCode.USER_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.USER_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (지갑 주인이 아닌 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val balance = 10000
            val walletUser = userRepository.save(UserEntity())
            val wallet = walletRepository.save(WalletEntity(userId = walletUser.userId!!, balance = balance))
            val amount = 1000

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.charge(
                    walletId = wallet.walletId!!,
                    userId = user.userId!!,
                    amount
                )
            }

            //then
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code, exception.code)
        }

        @Test
        fun `실패 (충전 금액이 음수인 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            val balance = 10000
            val wallet = walletRepository.save(WalletEntity(userId = user.userId!!, balance = balance))
            val amount = -1000

            //when
            val exception = assertThrows<CustomException> {
                walletFacade.charge(
                    walletId = wallet.walletId!!,
                    userId = user.userId!!,
                    amount
                )
            }

            //then
            assertEquals(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT.message, exception.message)
            assertEquals(ErrorCode.WALLET_INVALID_CHARGE_AMOUNT.code, exception.code)
        }
    }


}