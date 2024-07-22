package kr.com.hhp.concertreservationapiserver.token.presentation.scheduler

import kr.com.hhp.concertreservationapiserver.token.business.domain.service.TokenQueueService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TokenExpiryUpdater(private val tokenQueueService: TokenQueueService) {

    private val logger: Logger = LoggerFactory.getLogger(TokenExpiryUpdater::class.java)

    // 10분마다 실행 (0분, 10분, 20분, 30분, 40분, 50분)
    @Scheduled(cron = "0 0/10 * * * ?")
    @Transactional
    fun checkAndUpdateTokenExpiration() {
        logger.info("토큰 큐 만료 Scheduler 실행")
        tokenQueueService.checkAndUpdateTokenExpiration()
        logger.info("토큰 큐 만료 Scheduler 종료")
    }
}