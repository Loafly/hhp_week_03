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
        const val MAX_IN_PROGRESS_SIZE = 100L
    }

    // 매분 20초마다 실행
    @Scheduled(cron = "20 * * * * *")
    @Transactional
    fun updateWaitingAllTokenToInProgress() {
        logger.info("토큰 큐 업데이트(W -> P) Scheduler 실행")
        tokenQueueService.updateWaitingAllTokenToInProgress(MAX_IN_PROGRESS_SIZE)
        logger.info("토큰 큐 업데이트(W -> P) Scheduler 종료")
    }
}