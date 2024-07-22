package kr.com.hhp.concertreservationapiserver.concert.presentation.scheduler

import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertReservationHistoryService
import kr.com.hhp.concertreservationapiserver.concert.business.domain.service.ConcertSeatService
import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertReservationStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ConcertSeatReservationExpiryUpdater(private val concertSeatService: ConcertSeatService,
                                          private val concertReservationHistoryService: ConcertReservationHistoryService
) {

    private val logger: Logger = LoggerFactory.getLogger(ConcertSeatReservationExpiryUpdater::class.java)

    // 5분마다 임시 예약된 좌석 확인 후 만료된 경우 임시 예약 취소
    @Scheduled(cron = "0 */5 * * * *")
    fun releaseExpiredReservation() {
        logger.info("콘서트 임시예약 좌석 만료 Scheduler 실행")
        val concertSeats = concertSeatService.releaseExpiredReservations()
        concertReservationHistoryService.createAll(
            concertSeatIds = concertSeats.map { it.concertSeatId!! },
            status = ConcertReservationStatus.A
        )
        logger.info("콘서트 임시예약 좌석 만료 Scheduler 종료")
    }
}