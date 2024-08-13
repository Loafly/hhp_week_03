package kr.com.hhp.concertreservationapiserver.common.infra.event

import kr.com.hhp.concertreservationapiserver.common.domain.event.SendMessageEvent
import kr.com.hhp.concertreservationapiserver.common.domain.event.SendMessageEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class SendMessageEventApplicationPublisher(private val applicationEventPublisher: ApplicationEventPublisher): SendMessageEventPublisher {

    // 이벤트가 트랜잭션 안에 있는 경우 commit 이후에 실행될 수 있도록 처리
    override fun publishEvent(event: SendMessageEvent) {
        if(TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() {
                    applicationEventPublisher.publishEvent(event)
                }
            })
        } else {
            applicationEventPublisher.publishEvent(event)
        }
    }
}