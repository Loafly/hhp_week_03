package kr.com.hhp.concertreservationapiserver.common.domain

import kr.com.hhp.concertreservationapiserver.common.domain.event.SendMessageChannel

interface SendMessage {
    fun send(channel: SendMessageChannel, message: String)
}