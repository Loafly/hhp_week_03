package kr.com.hhp.concertreservationapiserver.concert.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import org.springframework.stereotype.Service

@Service
class ConcertService(private val concertRepository: ConcertRepository) {

    fun getByConcertId(concertId: Long): ConcertEntity {
        return concertRepository.findByConcertId(concertId)
            ?: throw CustomException(ErrorCode.CONCERT_NOT_FOUND)
    }


}