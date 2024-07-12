package kr.com.hhp.concertreservationapiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
class ConcertReservationApiServerApplication

fun main(args: Array<String>) {
    runApplication<ConcertReservationApiServerApplication>(*args)
}
