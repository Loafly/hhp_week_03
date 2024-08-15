package kr.com.hhp.concertreservationapiserver.common.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity
import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.common.domain.repository.OutBoxRepository
import org.springframework.stereotype.Service

@Service
class OutBoxService(private val outBoxRepository: OutBoxRepository) {

    fun create(payload: String): OutBoxEntity {
        return outBoxRepository.save(
            OutBoxEntity(payload = payload, eventStatus = EventStatus.INIT)
        )
    }

    fun updateOutBoxStatus(outBoxId: Long) {
        val outBoxEntity = outBoxRepository.findById(outBoxId) ?: throw CustomException(ErrorCode.OUT_BOX_NOT_FOUNT)

    }
}