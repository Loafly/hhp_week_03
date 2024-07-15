package kr.com.hhp.concertreservationapiserver.concert.infra.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table
@Entity(name = "concert")
class ConcertEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var concertId: Long? = null
)