package kr.com.hhp.concertreservationapiserver.common.infra.message

import com.slack.api.Slack
import com.slack.api.methods.SlackApiException
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import kr.com.hhp.concertreservationapiserver.common.domain.message.SendMessage
import kr.com.hhp.concertreservationapiserver.common.domain.event.SendMessageChannel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class SlackSendMessage(@Value("\${slack.token}") private val token: String) : SendMessage {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(channel: SendMessageChannel, message: String) {
        val channelAddress = when (channel) {
            SendMessageChannel.CONCERT_RESERVATION -> "#항해플러스_5기_콘서트예약알림"
            SendMessageChannel.CONCERT_PAYMENT -> "#항해플러스_5기_콘서트결제알림"
            else -> "#error..."
        }

        try{
            val methods = Slack.getInstance().methods(token)

            val request = ChatPostMessageRequest.builder()
                .channel(channelAddress)
                .text(message)
                .build()

            val response = methods.chatPostMessage(request)

            if (!response.isOk) {
                log.error("Error sending message to Slack: ${response.error}")
            } else {
                log.info("Message sent to Slack channel: $channel")
            }
        } catch (e: SlackApiException) {
            log.error("Slack API exception: ${e.message}", e)
        } catch (e: IOException) {
            log.error("IO exception: ${e.message}", e)
        }

    }
}