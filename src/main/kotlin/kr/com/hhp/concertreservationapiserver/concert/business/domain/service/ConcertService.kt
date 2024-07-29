package kr.com.hhp.concertreservationapiserver.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatPaymentHistoryEntity
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertReservationHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatPaymentHistoryRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertSeatRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ConcertService(
    private val concertRepository: ConcertRepository,
    private val concertDetailRepository: ConcertDetailRepository,
    private val concertSeatRepository: ConcertSeatRepository,
    private val concertReservationHistoryRepository: ConcertReservationHistoryRepository,
    private val concertSeatPaymentHistoryRepository: ConcertSeatPaymentHistoryRepository,
) {

    // 예약 가능 날짜 조회
    fun getAllAvailableReservationDetail(concertId: Long, reservationDateTime: LocalDateTime): List<ConcertDetailEntity> {
        // 실제 존재하는 concertId가 맞는지 확인
        val concert = concertRepository.findByConcertId(concertId) ?: throw CustomException(ErrorCode.CONCERT_NOT_FOUND)
        val availableReservationConcertDetails = concertDetailRepository.findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
            concertId = concert.concertId!!,
            remainingSeatCount = 0,
            reservationStartDateTime = reservationDateTime,
            reservationEndDateTime = reservationDateTime
        )

        return availableReservationConcertDetails
    }

    // 예약 가능 좌석 조회
    fun getAllAvailableReservationSeat(concertDetailId: Long): List<ConcertSeatEntity> {
        val concertDetail = concertDetailRepository.findByConcertDetailId(concertDetailId)
            ?: throw CustomException(ErrorCode.CONCERT_DETAIL_NOT_FOUND)

        return concertSeatRepository.findAllByConcertDetailIdAndReservationStatus(
            concertDetailId = concertDetailId,
            reservationStatus = ConcertReservationStatus.A
        )
    }


    // 좌석 임시 예약
    @Transactional
    fun reserveSeatToTemporary(concertSeatId: Long, userId: Long) : ConcertSeatEntity {
        val concertSeat = concertSeatRepository.findByConcertSeatIdWithSLock(concertSeatId) ?: throw CustomException(ErrorCode.CONCERT_SEAT_NOT_FOUND)
        val concertDetail = concertDetailRepository.findByConcertDetailId(concertSeat.concertDetailId) ?: throw CustomException(ErrorCode.CONCERT_DETAIL_NOT_FOUND)
        concertDetail.throwExceptionIfNotReservationPeriod();

        concertSeat.updateReservationStatusT(userId)

        val concertReservationHistory = ConcertReservationHistoryEntity(
            concertSeatId = concertSeatId,
            status = concertSeat.reservationStatus
        )

        concertReservationHistoryRepository.save(concertReservationHistory)

        return concertSeatRepository.save(concertSeat)
    }

    //예약된 좌석 결제
    fun payForTemporaryReservedSeatToConfirmedReservation(concertSeatId: Long, userId: Long, walletId: Long): ConcertSeatEntity {
        val concertSeat = concertSeatRepository.findByConcertSeatId(concertSeatId) ?: throw CustomException(ErrorCode.CONCERT_SEAT_NOT_FOUND)
        concertSeat.updateReservationStatusC(userId)

        val concertReservationHistory = ConcertReservationHistoryEntity(
            concertSeatId = concertSeatId, status = concertSeat.reservationStatus
        )

        concertReservationHistoryRepository.save(concertReservationHistory)

        val concertSeatPaymentHistory = ConcertSeatPaymentHistoryEntity(
            concertSeatId = concertSeatId, price = concertSeat.price, walletId = walletId
        )

        concertSeatPaymentHistoryRepository.save(concertSeatPaymentHistory)

        return concertSeatRepository.save(concertSeat)
    }

    // 콘서트 예약 시간 초과시 만료 처리
    fun releaseExpiredReservations() {
        val expiredConcertSeats = concertSeatRepository.findAllByReservationStatusAndUpdatedAtIsAfter(
            ConcertReservationStatus.T, LocalDateTime.now().minusMinutes(30)
        ).onEach {it.expiredTemporaryReservation()}

        val concertSeats = concertSeatRepository.saveAll(expiredConcertSeats)

        val concertReservationHistories = concertSeats.map {
            ConcertReservationHistoryEntity(
                concertSeatId = it.concertSeatId!!,
                status = it.reservationStatus
            )
        }

        concertReservationHistoryRepository.saveAll(concertReservationHistories)
    }

}