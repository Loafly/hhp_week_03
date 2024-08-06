package kr.com.hhp.concertreservationapiserver.unit.wallet.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletBalanceType
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletHistoryEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletHistoryRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.then

@ExtendWith(MockitoExtension::class)
class WalletServiceTest {

    @Mock
    private lateinit var walletRepository: WalletRepository

    @Mock
    private lateinit var walletHistoryRepository: WalletHistoryRepository

    @InjectMocks
    private lateinit var walletService: WalletService

    @Nested
    @DisplayName("지갑 조회 (userId)")
    inner class GetByUserIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val userId = 1L
            val expectedWallet = WalletEntity(userId = userId)
            given(walletRepository.findByUserId(userId)).willReturn(expectedWallet)

            //when
            val wallet = walletService.getByUserId(userId)

            //then
            then(walletRepository).should().findByUserId(userId)
            assertEquals(expectedWallet, wallet)
        }

        @Test
        fun `실패 (지갑이 존재하지 않는 경우)`() {
            //given
            val userId = 1L
            given(walletRepository.findByUserId(userId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                walletService.getByUserId(userId)
            }

            //then
            then(walletRepository).should().findByUserId(userId)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
        }
    }


    @Nested
    @DisplayName("지갑 조회 (userId and walletId)")
    inner class GetByWalletIdAndUserIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val userId = 1L
            val walletId = 1L
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId)
            given(walletRepository.findByWalletId(walletId)).willReturn(expectedWallet)

            //when
            val wallet = walletService.getByWalletIdAndUserId(walletId = walletId, userId = userId)

            //then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals(expectedWallet, wallet)
        }

        @Test
        fun `실패 (지갑이 존재하지 않는 경우)`() {
            //given
            val userId = 1L
            val walletId = 1L
            given(walletRepository.findByWalletId(walletId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                walletService.getByWalletIdAndUserId(walletId = walletId, userId = userId)
            }

            //then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (유저가 다른 경우)`() {
            //given
            val userId = 1L
            val walletUserId = 2L
            val walletId = 1L
            val expectedWallet = WalletEntity(walletId = walletId, userId = walletUserId)
            given(walletRepository.findByWalletId(walletId)).willReturn(expectedWallet)

            //when
            val exception = assertThrows<CustomException> {
                walletService.getByWalletIdAndUserId(walletId = walletId, userId = userId)
            }

            //then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code, exception.code)
        }
    }


    @Nested
    @DisplayName("잔액 업데이트")
    inner class UpdateBalanceTest {

        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = 1000
            val walletBalanceType = WalletBalanceType.C
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId)

            given(walletRepository.findByWalletIdWithXLock(walletId)).willReturn(expectedWallet)

            val expectedWalletHistory = WalletHistoryEntity(
                walletId = walletId,
                amount = amount,
                balance = expectedWallet.balance + amount,
                balanceType = walletBalanceType
            )
            given(walletHistoryRepository.save(any())).willReturn(expectedWalletHistory)
            given(walletRepository.save(any())).willReturn(expectedWallet)

            //when
            val wallet = walletService.updateBalance(
                walletId = walletId,
                userId = userId,
                amount = amount,
                balanceType = walletBalanceType
            )

            //then
            then(walletRepository).should().findByWalletIdWithXLock(walletId)
            then(walletHistoryRepository).should().save(any())

            assertEquals(amount, wallet.balance)
        }

        @Test
        fun `실패 (충전 금액이 음수인 경우)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = -1000
            val walletBalanceType = WalletBalanceType.C

            //when
            val exception = assertThrows<CustomException> {
                walletService.updateBalance(
                    walletId = walletId,
                    userId = userId,
                    amount = amount,
                    balanceType = walletBalanceType
                )
            }

            //then
            assertEquals(ErrorCode.WALLET_INVALID_AMOUNT.message, exception.message)
            assertEquals(ErrorCode.WALLET_INVALID_AMOUNT.code, exception.code)
        }

        @Test
        fun `실패 (지갑이 존재하지 않는 경우)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = 1000
            val walletBalanceType = WalletBalanceType.C

            given(walletRepository.findByWalletIdWithXLock(walletId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException> {
                walletService.updateBalance(
                    walletId = walletId,
                    userId = userId,
                    amount = amount,
                    balanceType = walletBalanceType
                )
            }

            //then
            then(walletRepository).should().findByWalletIdWithXLock(walletId)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
        }

        @Test
        fun `실패 (유저가 다른 경우)`() {
            //given
            val walletId = 1L
            val walletUserId = 1L
            val userId = 2L
            val amount = 1000
            val walletBalanceType = WalletBalanceType.C
            val expectedWallet = WalletEntity(walletId = walletId, userId = walletUserId)

            given(walletRepository.findByWalletIdWithXLock(walletId)).willReturn(expectedWallet)

            //when
            val exception = assertThrows<CustomException> {
                walletService.updateBalance(
                    walletId = walletId,
                    userId = userId,
                    amount = amount,
                    balanceType = walletBalanceType
                )
            }

            //then
            then(walletRepository).should().findByWalletIdWithXLock(walletId)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.message, exception.message)
            assertEquals(ErrorCode.WALLET_USER_ID_IS_MIS_MATCH.code, exception.code)
        }

        @Test
        fun `실패 (잔액보다 사용 금액이 큰 경우)`() {
            //given
            val walletId = 1L
            val userId = 1L
            val amount = 1000
            val walletBalanceType = WalletBalanceType.U
            val expectedWallet = WalletEntity(walletId = walletId, userId = userId)

            given(walletRepository.findByWalletIdWithXLock(walletId)).willReturn(expectedWallet)

            //when
            val exception = assertThrows<CustomException> {
                walletService.updateBalance(
                    walletId = walletId,
                    userId = userId,
                    amount = amount,
                    balanceType = walletBalanceType
                )
            }

            //then
            then(walletRepository).should().findByWalletIdWithXLock(walletId)
            assertEquals(ErrorCode.WALLET_INVALID_BALANCE.message, exception.message)
            assertEquals(ErrorCode.WALLET_INVALID_BALANCE.code, exception.code)
        }
    }
}