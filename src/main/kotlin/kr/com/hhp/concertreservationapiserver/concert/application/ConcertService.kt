package kr.com.hhp.concertreservationapiserver.concert.application

import kr.com.hhp.concertreservationapiserver.concert.application.exception.ConcertNotFoundException
import kr.com.hhp.concertreservationapiserver.concert.domain.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import org.springframework.stereotype.Service

@Service
class ConcertService(private val concertRepository: ConcertRepository) {

    fun getByConcertId(concertId: Long): ConcertEntity {
        return concertRepository.findByConcertId(concertId)
            ?: throw ConcertNotFoundException("Concert가 존재하지 않습니다. concertId : $concertId")
    }


}