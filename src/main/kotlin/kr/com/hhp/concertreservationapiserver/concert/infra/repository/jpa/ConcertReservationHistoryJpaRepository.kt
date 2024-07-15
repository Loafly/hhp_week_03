package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertReservationHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConcertReservationHistoryJpaRepository: JpaRepository<ConcertReservationHistoryEntity, Long> {
}