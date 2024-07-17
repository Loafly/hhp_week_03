package kr.com.hhp.concertreservationapiserver.unit.wallet.application

import kr.com.hhp.concertreservationapiserver.user.domain.exception.UserIdMisMatchException
import kr.com.hhp.concertreservationapiserver.wallet.domain.service.WalletService
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.InvalidChargeAmountException
import kr.com.hhp.concertreservationapiserver.wallet.domain.exception.WalletNotFoundException
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class WalletServiceTest {

    @Mock
    private lateinit var walletRepository: WalletRepository

    @InjectMocks
    private lateinit var walletService: WalletService

    @Nested
    @DisplayName("지갑 조회")
    inner class GetByWalletIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val walletId = 1L
            val userId = 1L
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId)
            given(walletRepository.findByWalletId(walletId)).willReturn(expectedWallet)

            // when
            val wallet = walletService.getByWalletId(walletId)

            // then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals(expectedWallet.walletId, wallet.walletId)
            assertEquals(expectedWallet.userId, wallet.userId)
            assertEquals(expectedWallet.balance, wallet.balance)
        }

        @Test
        fun `실패 (사용자가 존재하지 않는 경우)`() {
            // given
            val walletId = 1L
            given(walletRepository.findByWalletId(walletId)).willReturn(null)

            // when
            val exception = assertThrows<WalletNotFoundException> {
                walletService.getByWalletId(walletId)
            }

            // then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals("Wallet이 존재하지 않습니다. walletId : $walletId", exception.message)
        }
    }

    @Nested
    @DisplayName("지갑 조회(userId)")
    inner class GetByUserIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val walletId = 1L
            val userId = 1L
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId)
            given(walletRepository.findByUserId(userId)).willReturn(expectedWallet)

            // when
            val wallet = walletService.getByUserId(userId)

            // then
            then(walletRepository).should().findByUserId(userId)
            assertEquals(expectedWallet.walletId, wallet.walletId)
            assertEquals(expectedWallet.userId, wallet.userId)
            assertEquals(expectedWallet.balance, wallet.balance)
        }

        @Test
        fun `실패 (사용자가 존재하지 않는 경우)`() {
            // given
            val userId = 1L
            given(walletRepository.findByUserId(userId)).willReturn(null)

            // when
            val exception = assertThrows<WalletNotFoundException> {
                walletService.getByUserId(userId)
            }

            // then
            then(walletRepository).should().findByUserId(userId)
            assertEquals("Wallet이 존재하지 않습니다. userId : $userId", exception.message)
        }
    }

    @Nested
    @DisplayName("잔액 충전")
    inner class ChargeTest {

        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = 1000

            val initialWallet = WalletEntity(walletId = walletId, userId = userId)
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId, balance = amount)
            given(walletRepository.findByWalletId(walletId)).willReturn(initialWallet)
            given(walletRepository.save(any())).willReturn(expectedWallet)

            //when
            val chargedWallet = walletService.charge(
                walletId = walletId,
                userId = userId,
                amount = amount
            )

            //then
            then(walletRepository).should().findByWalletId(walletId)
            then(walletRepository).should().save(any())
            assertEquals(expectedWallet.walletId, chargedWallet.walletId)
            assertEquals(expectedWallet.userId, chargedWallet.userId)
            assertEquals(expectedWallet.balance, chargedWallet.balance)
        }

        @Test
        fun `실패 (유저 Id와 Wallet이 가진 유저 Id가 상이한 경우)`() {
            //given
            val walletId = 1L
            val userId = 2L
            val walletUserId = 1L
            val amount = 1000

            val initialWallet = WalletEntity(walletId = walletId, userId = walletUserId)
            given(walletRepository.findByWalletId(walletId)).willReturn(initialWallet)

            //when
            val exception = assertThrows<UserIdMisMatchException> {
                walletService.charge(
                    walletId = walletId,
                    userId = userId,
                    amount = amount
                )
            }

            //then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals("유저Id가 일치하지 않습니다. userId : $userId, wallet.userId : $walletUserId", exception.message)
        }

        @Test
        fun `실패 (충전 금액이 음수인 경우)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = -1000

            val initialWallet = WalletEntity(walletId = walletId, userId = userId)
            given(walletRepository.findByWalletId(walletId)).willReturn(initialWallet)

            //when
            val exception = assertThrows<InvalidChargeAmountException> {
                walletService.charge(
                    walletId = walletId,
                    userId = userId,
                    amount = amount
                )
            }

            //then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals("충전 금액은 양수여야 합니다. amount : $amount", exception.message)
        }
    }
}