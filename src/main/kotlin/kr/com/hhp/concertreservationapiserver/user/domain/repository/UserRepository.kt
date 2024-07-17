package kr.com.hhp.concertreservationapiserver.user.domain.repository

import kr.com.hhp.concertreservationapiserver.user.infra.entity.UserEntity

interface UserRepository {

    fun findByUserId(userId: Long): UserEntity?
    fun save(userEntity: UserEntity): UserEntity
}