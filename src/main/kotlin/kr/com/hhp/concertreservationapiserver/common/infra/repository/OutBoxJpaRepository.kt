package kr.com.hhp.concertreservationapiserver.common.infra.repository

import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OutBoxJpaRepository: JpaRepository<OutBoxEntity, Long> {
    fun findByOutBoxId(id: Long): OutBoxEntity?
    fun findAllByEventStatus(eventStatus: EventStatus): List<OutBoxEntity>
}