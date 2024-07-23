package kr.com.hhp.concertreservationapiserver.unit.wallet.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.service.WalletService
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
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
            val exception = assertThrows<CustomException> {
                walletService.getByWalletId(walletId)
            }

            // then
            then(walletRepository).should().findByWalletId(walletId)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
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
            val exception = assertThrows<CustomException> {
                walletService.getByUserId(userId)
            }

            // then
            then(walletRepository).should().findByUserId(userId)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.WALLET_NOT_FOUND.code, exception.code)
        }
    }
}