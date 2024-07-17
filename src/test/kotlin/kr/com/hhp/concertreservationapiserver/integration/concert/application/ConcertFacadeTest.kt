package kr.com.hhp.concertreservationapiserver.integration.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertSeatRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.token.domain.repository.TokenQueueRepository
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueEntity
import kr.com.hhp.concertreservationapiserver.token.infra.entity.TokenQueueStatus
import kr.com.hhp.concertreservationapiserver.user.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.wallet.domain.repository.WalletRepository
import kr.com.hhp.concertreservationapiserver.wallet.infra.entity.WalletEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class ConcertFacadeTest {

    @Autowired
    private lateinit var concertFacade: ConcertFacade

    @Autowired
    lateinit var tokenQueueRepository: TokenQueueRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var concertRepository: ConcertRepository

    @Autowired
    lateinit var concertDetailRepository: ConcertDetailRepository

    @Autowired
    lateinit var concertSeatRepository: ConcertSeatRepository

    @Autowired
    lateinit var walletRepository: WalletRepository

    @Test
    fun `예약 가능 시간 리스트 조회`() {
        //given
        val user = userRepository.save(UserEntity())
        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

        val concert = concertRepository.save(ConcertEntity())
        val concertDetail = concertDetailRepository.save(
            ConcertDetailEntity(
                concertId = concert.concertId!!,
                reservationStartDateTime = LocalDateTime.now().minusDays(10),
                reservationEndDateTime = LocalDateTime.now().plusDays(10)
            )
        )

        //when
        val reservationDetails = concertFacade.getAllAvailableReservationDetail(
            token = tokenQueue.token, concertId = concert.concertId!!, reservationDateTime = LocalDateTime.now()
        )

        val reservationDetail = reservationDetails[0]

        //then
        assertEquals(1, reservationDetails.size)
        assertNotNull(reservationDetail)
        assertEquals(concertDetail.concertId, reservationDetail.concertId)
        assertEquals(concertDetail.reservationStartDateTime, reservationDetail.reservationStartDateTime)
        assertEquals(concertDetail.reservationEndDateTime, reservationDetail.reservationEndDateTime)
    }

    @Test
    fun `예약 가능 좌석 조회`() {
        //given
        val user = userRepository.save(UserEntity())
        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

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

        //when
        val availableReservationSeats = concertFacade.getAllAvailableReservationSeat(
            token = tokenQueue.token, concertDetailId = concertDetail.concertDetailId!!
        )
        val availableReservationSeat = availableReservationSeats[0]

        //then
        assertEquals(1, availableReservationSeats.size)
        assertNotNull(availableReservationSeat)
        assertEquals(concertSeat.concertSeatId, availableReservationSeat.concertSeatId)
        assertEquals(concertSeat.reservationStatus.toString(), availableReservationSeat.reservationStatus)
        assertEquals(concertSeat.seatNumber, availableReservationSeat.seatNumber)
        assertEquals(concertSeat.price, availableReservationSeat.price)

    }

    @Test
    fun `좌석 임시 예약`() {
        //given
        val user = userRepository.save(UserEntity())
        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))

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

        //when
        val temporaryConcertSeat = concertFacade.reserveSeatToTemporary(
            token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
        )

        //then
        assertNotNull(temporaryConcertSeat)
        assertEquals(concertSeat.concertSeatId, temporaryConcertSeat.concertSeatId)
        assertEquals(concertSeat.seatNumber, temporaryConcertSeat.seatNumber)
        assertEquals(concertSeat.price, temporaryConcertSeat.price)
        assertNotEquals(concertSeat.reservationStatus.toString(), temporaryConcertSeat.reservationStatus)
    }

    @Test
    fun `예약된 좌석 결제`() {
        //given
        val user = userRepository.save(UserEntity())
        val tokenQueue = tokenQueueRepository.save(TokenQueueEntity(userId = user.userId!!, status = TokenQueueStatus.P))
        val wallet = walletRepository.save(WalletEntity(userId = user.userId!!))

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
                seatNumber = 1,
                reservationStatus = ConcertReservationStatus.T,
                concertDetailId = concertDetail.concertDetailId!!,
                price = 1000,
                userId = user.userId!!
            )
        )

        //when
        concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
            token = tokenQueue.token, concertSeatId = concertSeat.concertSeatId!!
        )
    }
}