package kr.com.hhp.concertreservationapiserver.user.application

import kr.com.hhp.concertreservationapiserver.user.application.exception.UserNotFoundException
import kr.com.hhp.concertreservationapiserver.user.domain.UserRepository
import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getByUserId(userId: Long): UserEntity {
        return userRepository.findByUserId(userId)?: throw UserNotFoundException("User가 존재하지 않습니다. userId : $userId")
    }

    fun save(): UserEntity {
        return userRepository.save(UserEntity())
    }
}