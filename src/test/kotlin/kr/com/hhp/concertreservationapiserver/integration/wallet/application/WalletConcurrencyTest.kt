package kr.com.hhp.concertreservationapiserver.integration.wallet.application

import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.repository.jpa.UserJpaRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.application.WalletFacade
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa.WalletJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class WalletConcurrencyTest {

    @Autowired
    private lateinit var walletFacade: WalletFacade

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    lateinit var walletRepository: WalletRepository

    @Autowired
    lateinit var walletJpaRepository: WalletJpaRepository

    // 이게 맞는건지....
    @AfterEach
    fun cleanup() {
        // 테스트 후 데이터 정리
        userJpaRepository.deleteAll()
        walletJpaRepository.deleteAll()
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

            val numberOfThreads = 100
            val latch = CountDownLatch(numberOfThreads)
            val executorService = Executors.newFixedThreadPool(numberOfThreads)

            //when
            for (i in 1..numberOfThreads) {
                executorService.submit {
                    try {
                        walletFacade.charge(
                            walletId = wallet.walletId!!,
                            userId = user.userId!!,
                            amount
                        )
                    } finally {
                        latch.countDown()  // 스레드가 작업을 마칠 때마다 카운트다운
                    }
                }
            }

            latch.await()  // 모든 스레드가 작업을 마칠 때까지 대기
            executorService.shutdown()

            val newWallet = walletRepository.findByWalletId(walletId = wallet.walletId!!)

            //then
            assertEquals(balance + (amount * numberOfThreads), newWallet!!.balance)
        }
    }
}