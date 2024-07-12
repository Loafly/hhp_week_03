package kr.com.hhp.concertreservationapiserver.unit.wallet.application

import kr.com.hhp.concertreservationapiserver.wallet.application.WalletHistoryService
import kr.com.hhp.concertreservationapiserver.wallet.domain.WalletHistoryRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletHistoryEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.then

@ExtendWith(MockitoExtension::class)
class WalletHistoryServiceTest {

    @Mock
    private lateinit var walletHistoryRepository: WalletHistoryRepository

    @InjectMocks
    private lateinit var walletHistoryService: WalletHistoryService

    @Nested
    @DisplayName("지갑 히스토리 저장")
    inner class SaveTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val walletId = 1L
            val balance = 1500
            val amount = 1000

            val expectedWalletHistory = WalletHistoryEntity(walletId = walletId, amount = amount, balance = balance)
            given(walletHistoryRepository.save(any())).willReturn(expectedWalletHistory)

            //when
            val walletHistory = walletHistoryService.create(walletId = walletId, amount = amount, balance = balance)

            //then
            then(walletHistoryRepository).should().save(any())
            assertEquals(expectedWalletHistory.walletId, walletHistory.walletId)
            assertEquals(expectedWalletHistory.amount, walletHistory.amount)
            assertEquals(expectedWalletHistory.balance, walletHistory.balance)
        }
    }
}