package kr.com.hhp.concertreservationapiserver.concert.presentation.event

import kr.com.hhp.concertreservationapiserver.concert.business.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.business.domain.event.ConcertEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ConcertReservationEventListener(private val concertFacade: ConcertFacade) {

    @Async
    @EventListener
    fun handle(event: ConcertEvent.Reservation) {
        // 콘서트 임시 예약 이후 처리
        concertFacade.reserveSeat(concertSeatId = event.concertSeatId)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: ConcertEvent.Pay) {
        // 콘서트 좌석 결제 이후 처리
        concertFacade.paymentSeat(
            walletId = event.walletId,
            userId = event.userId,
            price = event.price,
            concertSeatId = event.concertSeatId,
            token = event.token
        )
    }
}