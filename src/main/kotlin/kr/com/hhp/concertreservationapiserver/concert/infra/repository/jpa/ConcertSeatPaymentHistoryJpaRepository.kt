package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertSeatPaymentHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertSeatPaymentHistoryJpaRepository: JpaRepository<ConcertSeatPaymentHistoryEntity, Long> {

}