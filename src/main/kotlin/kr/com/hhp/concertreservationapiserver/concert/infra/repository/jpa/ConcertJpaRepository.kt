package kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConcertJpaRepository: JpaRepository<ConcertEntity, Long> {

    fun findByConcertId(concertId: Long): ConcertEntity?
}