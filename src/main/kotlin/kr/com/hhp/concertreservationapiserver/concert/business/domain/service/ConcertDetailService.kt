package kr.com.hhp.concertreservationapiserver.concert.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.business.domain.repository.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertDetailEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ConcertDetailService(private val concertDetailRepository: ConcertDetailRepository) {

    fun getAllAvailableReservationByConcertId(
        concertId: Long, reservationDateTime: LocalDateTime
    ): List<ConcertDetailEntity> {
        return concertDetailRepository.findAllByConcertIdAndRemainingSeatCountNotAndReservationStartDateTimeIsBeforeAndReservationEndDateTimeIsAfter(
            concertId = concertId,
            remainingSeatCount = 0,
            reservationStartDateTime = reservationDateTime,
            reservationEndDateTime = reservationDateTime
        )
    }

    fun getByConcertDetailId(concertDetailId: Long): ConcertDetailEntity {
        return concertDetailRepository.findByConcertDetailId(concertDetailId)
            ?: throw CustomException(ErrorCode.CONCERT_DETAIL_NOT_FOUND)
    }

    fun throwExceptionIfNotReservationPeriod(concertDetailEntity: ConcertDetailEntity) {

        // 예약 기간이 시작되지 않은 경우
        if(concertDetailEntity.reservationStartDateTime.isAfter(LocalDateTime.now())) {
            throw CustomException(ErrorCode.CONCERT_RESERVATION_PERIOD_EARLY)
        }

        // 예약 기간이 끝난 경우
        if(concertDetailEntity.reservationEndDateTime.isBefore(LocalDateTime.now())) {
            throw CustomException(ErrorCode.CONCERT_RESERVATION_PERIOD_LATE)
        }
    }


}