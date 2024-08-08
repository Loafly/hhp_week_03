package kr.com.hhp.concertreservationapiserver.concert.business.domain.event

interface ConcertEventPublisher {

    // 임시 예약 시 발생해야하는 이벤트
    fun publishReservationEvent(concertSeatId: Long)

    // 결제 완료 시 발생해야하는 이벤트
    fun publishPaymentEvent(
        concertSeatId: Long,
        userId: Long,
        price: Int,
        walletId: Long,
        token: String
    )
}