package kr.com.hhp.concertreservationapiserver.concert.business.domain.repository

import kr.com.hhp.concertreservationapiserver.concert.business.domain.entity.ConcertEntity

interface ConcertRepository {
    fun save(concert: ConcertEntity): ConcertEntity
    fun findByConcertId(concertId: Long): ConcertEntity?
}