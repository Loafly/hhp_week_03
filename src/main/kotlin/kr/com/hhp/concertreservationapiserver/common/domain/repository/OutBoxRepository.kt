package kr.com.hhp.concertreservationapiserver.common.domain.repository

import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity

interface OutBoxRepository {
    fun save(outBoxEntity: OutBoxEntity): OutBoxEntity
    fun saveAll(outBoxEntities: List<OutBoxEntity>): List<OutBoxEntity>
    fun findById(id: Long): OutBoxEntity?
    fun findAllByEventStatus(eventStatus: EventStatus): List<OutBoxEntity>
}