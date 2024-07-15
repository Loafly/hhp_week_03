package kr.com.hhp.concertreservationapiserver.concert.infra.entity

enum class ConcertReservationStatus (private val description: String) {

    A("Available"),
    T("Temporary"),
    C("Confirmed");

    override fun toString(): String {
        return description
    }
}