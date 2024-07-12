package kr.com.hhp.concertreservationapiserver.concert.infra.repository

import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import kr.com.hhp.concertreservationapiserver.concert.infra.repository.jpa.ConcertJpaRepository
import org.springframework.stereotype.Repository

@Repository
class ConcertRepositoryImpl(private val concertJpaRepository: ConcertJpaRepository): ConcertRepository {
    override fun save(concert: ConcertEntity): ConcertEntity {
        return concertJpaRepository.save(concert)
    }

    override fun findByConcertId(concertId: Long): ConcertEntity? {
        return concertJpaRepository.findByConcertId(concertId)
    }
}