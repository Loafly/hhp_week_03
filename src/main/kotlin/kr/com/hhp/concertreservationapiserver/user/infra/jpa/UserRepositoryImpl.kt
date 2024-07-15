package kr.com.hhp.concertreservationapiserver.user.infra.jpa

import kr.com.hhp.concertreservationapiserver.user.domain.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
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