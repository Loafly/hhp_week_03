package kr.com.hhp.concertreservationapiserver.user.infra.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "user")
class UserEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long? = null

)