package kr.com.hhp.concertreservationapiserver.user.infra.repository

import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import kr.com.hhp.concertreservationapiserver.user.infra.repository.jpa.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val userJpaRepository: UserJpaRepository): UserRepository {

    override fun findByUserId(userId: Long): UserEntity? {
        return userJpaRepository.findByUserId(userId)
    }

    override fun save(userEntity: UserEntity): UserEntity {
        return userJpaRepository.save(userEntity)
    }

}