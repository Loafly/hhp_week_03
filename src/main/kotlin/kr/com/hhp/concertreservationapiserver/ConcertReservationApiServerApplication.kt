package kr.com.hhp.concertreservationapiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ConcertReservationApiServerApplication

fun main(args: Array<String>) {
    runApplication<ConcertReservationApiServerApplication>(*args)
}
