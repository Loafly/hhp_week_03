package kr.com.hhp.concertreservationapiserver.common.domain.event

interface SendMessageEventPublisher {

    fun publishEvent(event: SendMessageEvent)

}