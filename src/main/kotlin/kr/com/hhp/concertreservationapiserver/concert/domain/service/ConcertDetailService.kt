package kr.com.hhp.concertreservationapiserver.concert.domain.service

import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertDetailNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.exception.ConcertReservationPeriodException
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertDetailRepository
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

    fun throwExceptionIfNotReservationPeriod(concertDetailEntity: ConcertDetailEntity) {

        // 현재 예약 시작시간 이후가 아닌 경우
        if(concertDetailEntity.reservationStartDateTime.isAfter(LocalDateTime.now())) {
            throw ConcertReservationPeriodException("예약 시작 시간이 아닙니다.")
        }

        // 현재 예약 종료시간 이전이 아닌 경우
        if(concertDetailEntity.reservationEndDateTime.isBefore(LocalDateTime.now())) {
            throw ConcertReservationPeriodException("예약 시작 시간이 아닙니다.")
        }
    }


}