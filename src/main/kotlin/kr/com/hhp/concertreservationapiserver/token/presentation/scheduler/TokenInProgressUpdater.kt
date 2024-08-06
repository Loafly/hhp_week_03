package kr.com.hhp.concertreservationapiserver.token.presentation.scheduler

import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class TokenInProgressUpdater(private val tokenQueueService: TokenQueueService) {

    private val logger: Logger = LoggerFactory.getLogger(TokenInProgressUpdater::class.java)

    companion object  {
        const val UPDATE_IN_PROGRESS_SIZE = 200L
    }

    /*
        - 1명의 사용자가, 콘서트 조회 / 임시 예약 / 결제 처리 예상 시간 -> 5분
        - 한번에 1000명의 사용자 처리 가능(예상) -> 1분당 200명
        - 분당 200명씩 W -> P로 변경
     */

    // 매분 20초마다 실행
    @Scheduled(cron = "20 * * * * *")
    @Transactional
    fun updateWaitingAllTokenToInProgress() {
        logger.info("토큰 큐 업데이트(W -> P) Scheduler 실행")
        tokenQueueService.updateWaitingAllTokenToInProgress(UPDATE_IN_PROGRESS_SIZE)
        logger.info("토큰 큐 업데이트(W -> P) Scheduler 종료")
    }
}