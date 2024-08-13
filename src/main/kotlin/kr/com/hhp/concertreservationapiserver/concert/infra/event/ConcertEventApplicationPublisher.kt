package kr.com.hhp.concertreservationapiserver.concert.infra.event

import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEventPublisher
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class ConcertEventApplicationPublisher(private val applicationEventPublisher: ApplicationEventPublisher) :
    ConcertEventPublisher {

    override fun publishReservationEvent(concertSeatId: Long) {
        applicationEventPublisher.publishEvent(ConcertEvent.Reservation(concertSeatId))
    }

    override fun publishPaymentEvent(
        concertSeatId: Long,
        userId: Long,
        price: Int,
        walletId: Long,
        token: String
    ) {
        applicationEventPublisher.publishEvent(
            ConcertEvent.Pay(
                concertSeatId = concertSeatId,
                userId = userId,
                price = price,
                walletId = walletId,
                token = token
            )
        )
    }

}