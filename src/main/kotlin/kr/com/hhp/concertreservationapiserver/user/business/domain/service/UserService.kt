package kr.com.hhp.concertreservationapiserver.user.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun getByUserId(userId: Long): UserEntity {
        return userRepository.findByUserId(userId)?: throw CustomException(ErrorCode.USER_NOT_FOUND)
    }

    fun save(): UserEntity {
        return userRepository.save(UserEntity())
    }
}