package kr.com.hhp.concertreservationapiserver.common.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity
import kr.com.hhp.concertreservationapiserver.common.domain.repository.OutBoxRepository
import org.springframework.stereotype.Service

@Service
class OutBoxService(private val outBoxRepository: OutBoxRepository) {

    fun create(payload: String, eventType: String): OutBoxEntity {
        return outBoxRepository.save(
            OutBoxEntity(payload = payload, eventType = eventType, eventStatus = EventStatus.INIT)
        )
    }

    fun save(outBoxEntity: OutBoxEntity): OutBoxEntity {
        return outBoxRepository.save(outBoxEntity)
    }
}