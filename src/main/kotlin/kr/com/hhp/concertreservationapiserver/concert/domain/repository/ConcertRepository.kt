package kr.com.hhp.concertreservationapiserver.concert.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity

interface ConcertRepository {
    fun save(concert: ConcertEntity): ConcertEntity
    fun findByConcertId(concertId: Long): ConcertEntity?
}