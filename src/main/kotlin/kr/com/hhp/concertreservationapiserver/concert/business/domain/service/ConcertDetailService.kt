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
}