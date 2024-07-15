package kr.com.hhp.concertreservationapiserver.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertDetailNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertDetailRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertDetailEntity
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
            ?: throw ConcertDetailNotFoundException("콘서트 상세가 존재하지 않습니다. concertDetailId : $concertDetailId")
    }


}