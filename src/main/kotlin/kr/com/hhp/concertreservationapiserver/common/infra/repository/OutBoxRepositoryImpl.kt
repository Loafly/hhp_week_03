package kr.com.hhp.concertreservationapiserver.common.infra.repository

import kr.com.hhp.concertreservationapiserver.common.domain.entity.EventStatus
import kr.com.hhp.concertreservationapiserver.common.domain.entity.OutBoxEntity
import kr.com.hhp.concertreservationapiserver.common.domain.repository.OutBoxRepository
import org.springframework.stereotype.Repository

@Repository
class OutBoxRepositoryImpl(private val outBoxJpaRepository: OutBoxJpaRepository): OutBoxRepository {
    override fun save(outBoxEntity: OutBoxEntity): OutBoxEntity {
        return outBoxJpaRepository.save(outBoxEntity)
    }

    override fun saveAll(outBoxEntities: List<OutBoxEntity>): List<OutBoxEntity> {
        return outBoxJpaRepository.saveAll(outBoxEntities)
    }

    override fun findById(id: Long): OutBoxEntity? {
        return outBoxJpaRepository.findByOutBoxId(id)
    }

    override fun findAllByEventStatus(eventStatus: EventStatus): List<OutBoxEntity> {
        return outBoxJpaRepository.findAllByEventStatus(eventStatus)
    }
}