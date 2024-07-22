package kr.com.hhp.concertreservationapiserver.token.business.domain.entity

enum class TokenQueueStatus (private val description: String) {
    W("Waiting"),      // 대기 중
    P("In Progress"),  // 진행 중
    C("Completed"),    // 완료됨
    F("Failed");       // 실패함

    override fun toString(): String {
        return description
    }
}