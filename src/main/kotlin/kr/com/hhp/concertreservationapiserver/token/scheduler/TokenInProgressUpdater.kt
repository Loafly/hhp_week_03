package kr.com.hhp.concertreservationapiserver.token.scheduler

import kr.com.hhp.concertreservationapiserver.token.domain.service.TokenQueueService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class TokenInProgressUpdater(private val tokenQueueService: TokenQueueService) {

    companion object  {
        const val MAX_IN_PROGRESS_SIZE = 100L
    }

    // 매분 20초마다 실행
    @Scheduled(cron = "20 * * * * *")
    @Transactional
    fun updateWaitingAllTokenToInProgress() {
        tokenQueueService.updateWaitingAllTokenToInProgress(MAX_IN_PROGRESS_SIZE)
    }
}