package kr.com.hhp.concertreservationapiserver.token.scheduler

import kr.com.hhp.concertreservationapiserver.token.application.TokenQueueService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TokenExpiryUpdater(private val tokenQueueService: TokenQueueService) {

    // 10분마다 실행 (0분, 10분, 20분, 30분, 40분, 50분)
    @Scheduled(cron = "0 0/10 * * * ?")
    @Transactional
    fun checkAndUpdateTokenExpiration() {
        tokenQueueService.checkAndUpdateTokenExpiration()
    }
}