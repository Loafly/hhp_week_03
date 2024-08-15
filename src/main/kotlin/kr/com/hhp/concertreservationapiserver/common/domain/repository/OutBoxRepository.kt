package kr.com.hhp.concertreservationapiserver.common.domain.repository

import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity

interface OutBoxRepository {
    fun save(outBoxEntity: OutBoxEntity): OutBoxEntity
    fun findById(id: Long): OutBoxEntity?
}