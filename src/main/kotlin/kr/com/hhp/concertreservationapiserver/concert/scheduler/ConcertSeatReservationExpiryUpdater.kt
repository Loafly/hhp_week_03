package kr.com.hhp.concertreservationapiserver.concert.scheduler

import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertReservationHistoryService
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertSeatService
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ConcertSeatReservationExpiryUpdater(private val concertSeatService: ConcertSeatService,
                                          private val concertReservationHistoryService: ConcertReservationHistoryService
) {

    // 5분마다 임시 예약된 좌석 확인 후 만료된 경우 임시 예약 취소
    @Scheduled(cron = "0 */5 * * * *")
    fun releaseExpiredReservation() {
        val concertSeats = concertSeatService.releaseExpiredReservations()
        concertReservationHistoryService.createAll(
            concertSeatIds = concertSeats.map { it.concertSeatId!! },
            status = ConcertReservationStatus.A
        )
    }
}