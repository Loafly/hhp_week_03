package kr.com.hhp.concertreservationapiserver.user.business.domain.repository

import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity

interface UserRepository {

    fun findByUserId(userId: Long): UserEntity?
    fun save(userEntity: UserEntity): UserEntity
}