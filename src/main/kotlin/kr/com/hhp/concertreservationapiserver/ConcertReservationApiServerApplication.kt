package kr.com.hhp.concertreservationapiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ConcertReservationApiServerApplication

fun main(args: Array<String>) {
    runApplication<ConcertReservationApiServerApplication>(*args)
}
