package kr.com.hhp.concertreservationapiserver.integration.concert.application

import kr.com.hhp.concertreservationapiserver.concert.business.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertDetailJpaRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertJpaRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertSeatJpaRepository
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.business.domain.entity.TokenQueueStatus
import kr.com.hhp.concertreservationapiserver.token.business.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.repository.jpa.TokenQueueJpaRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.repository.jpa.UserJpaRepository
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.entity.WalletEntity
import kr.com.hhp.concertreservationapiserver.wallet.business.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.repository.jpa.WalletJpaRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals

@SpringBootTest
class ConcertConcurrencyTest {

    @Autowired
    private lateinit var concertFacade: ConcertFacade

    @Autowired
    lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    lateinit var tokenQueueJpaRepository: TokenQueueJpaRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    lateinit var concertRepository: ConcertRepository

    @Autowired
    lateinit var concertJpaRepository: ConcertJpaRepository

    @Autowired
    lateinit var concertDetailRepository: ConcertDetailRepository

    @Autowired
    lateinit var concertDetailJpaRepository: ConcertDetailJpaRepository

    @Autowired
    lateinit var concertSeatRepository: ConcertSeatRepository

    @Autowired
    lateinit var concertSeatJpaRepository: ConcertSeatJpaRepository

    @Autowired
    lateinit var walletRepository: WalletRepository

    @Autowired
    lateinit var walletJpaRepository: WalletJpaRepository

    // 이게 맞는건지....
    @AfterEach
    fun cleanup() {
        // 테스트 후 데이터 정리
        tokenQueueJpaRepository.deleteAll()
        userJpaRepository.deleteAll()
        concertJpaRepository.deleteAll()
        concertDetailJpaRepository.deleteAll()
        concertSeatJpaRepository.deleteAll()
        walletJpaRepository.deleteAll()
    }

    @Nested
    @DisplayName("좌석 임시 예약")
    inner class ReserveSeatToTemporaryTest {
        @Test
        fun `성공 (50명의 사용자가 동시에 한 좌석을 예약한 경우)`() {
            //given

            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000
                )
            )
            // 동시성 테스트 를 위한 값
            val numberOfThreads = 50
            val latch = CountDownLatch(numberOfThreads)
            val executorService = Executors.newFixedThreadPool(numberOfThreads)

            val errorCount = AtomicInteger(0);

            // when
            for (i in 1..numberOfThreads) {
                executorService.submit {
                    try {
                        val user = userRepository.save(UserEntity())
                        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
                        concertFacade.reserveSeatToTemporary(
                            token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                        )
                    } catch (e: Exception) {
                        errorCount.incrementAndGet()
                    }
                    finally {
                        latch.countDown()  // 스레드가 작업을 마칠 때마다 카운트다운
                    }
                }
            }

            latch.await()  // 모든 스레드가 작업을 마칠 때까지 대기
            executorService.shutdown()

            assertEquals(numberOfThreads - 1, errorCount.get())
        }
    }


    @Nested
    @DisplayName("예약된 좌석 결제")
    inner class PayForTemporaryReservedSeatToConfirmedReservationTest {
        @Test
        fun `성공 (동일한 계정인 10명의 사용자가 동시에 한 좌석을 결제하는 경우)`() {
            //given
            val user = userRepository.save(UserEntity())
            walletRepository.save(WalletEntity(userId = user.userId!!, balance = 50000))
            val concert = concertRepository.save(ConcertEntity())
            val concertDetail = concertDetailRepository.save(
                ConcertDetailEntity(
                    concertId = concert.concertId!!,
                    reservationStartDateTime = LocalDateTime.now().minusDays(10),
                    reservationEndDateTime = LocalDateTime.now().plusDays(10)
                )
            )
            val concertSeat = concertSeatRepository.save(
                ConcertSeatEntity(
                    seatNumber = 1, concertDetailId = concertDetail.concertDetailId!!, price = 1000,
                    reservationStatus = ConcertReservationStatus.T, userId = user.userId!!
                )
            )

            // 동시성 테스트 를 위한 값
            val numberOfThreads = 15
            val latch = CountDownLatch(numberOfThreads)
            val executorService = Executors.newFixedThreadPool(numberOfThreads)

            val errorCount = AtomicInteger(0);

            // when
            for (i in 1..numberOfThreads) {
                executorService.submit {
                    try {
                        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
                        concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
                            token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
                        )
                    } catch (e: Exception) {
                        println("message : ${e.message}")
                        errorCount.incrementAndGet()
                    }
                    finally {
                        latch.countDown()  // 스레드가 작업을 마칠 때마다 카운트다운
                    }
                }
            }

            latch.await()  // 모든 스레드가 작업을 마칠 때까지 대기
            executorService.shutdown()

            assertEquals(numberOfThreads - 1, errorCount.get())
        }
    }
}