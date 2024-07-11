package kr.com.hhp.concertreservationapiserver.user.infra.repository.jpa

import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository: JpaRepository<UserEntity, Long> {

    fun findByUserId(userId: Long): UserEntity?
}