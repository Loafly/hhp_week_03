package kr.com.hhp.concertreservationapiserver.integration.wallet.application

import kr.com.hhp.concertreservationapiserver.user.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.wallet.application.WalletFacade
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WalletFacadeTest {

    @Autowired
    private lateinit var walletFacade: WalletFacade

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Test
    fun `지갑 잔액 조회`() {
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
    fun `지갑 잔액 충전`() {
        //given
        val user = userRepository.save(UserEntity())
        val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))
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
        assertEquals(wallet.balance + amount, balanceResponse.balance)
    }
}