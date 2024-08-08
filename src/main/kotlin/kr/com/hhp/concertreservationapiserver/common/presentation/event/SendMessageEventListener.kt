package kr.com.hhp.concertreservationapiserver.common.presentation.event

import kr.com.hhp.concertreservationapiserver.common.domain.SendMessage
import kr.com.hhp.concertreservationapiserver.common.domain.event.SendMessageEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class SendMessageEventListener(private val sendMessage: SendMessage) {

    @EventListener
    fun handle(event: SendMessageEvent) {
        sendMessage.send(channel = event.channel, message = event.message)
    }
}