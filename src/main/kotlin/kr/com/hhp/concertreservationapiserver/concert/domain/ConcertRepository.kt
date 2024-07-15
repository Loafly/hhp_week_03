package kr.com.hhp.concertreservationapiserver.concert.domain

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import org.springframework.stereotype.Repository

interface ConcertRepository {
    fun save(concert: ConcertEntity): ConcertEntity
    fun findByConcertId(concertId: Long): ConcertEntity?
}