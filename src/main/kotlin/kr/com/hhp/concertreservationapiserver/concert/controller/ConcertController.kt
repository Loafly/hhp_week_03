import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kr.com.hhp.concertreservationapiserver.common.ErrorResponse
import kr.com.hhp.concertreservationapiserver.concert.application.ConcertFacade
import kr.com.hhp.concertreservationapiserver.concert.controller.ConcertDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/concerts")
@Tag(name = "Concert")
class ConcertController(
    private val concertFacade: ConcertFacade
) {

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation = ConcertDto.DetailResponse::class)))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 InProgress 상태가 아닌 경우 (token == notInProgressToken)", value = "{ \"message\" : \"토큰 상태가 올바르지 않습니다.\"}"),
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우 (token == token)", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                    ExampleObject(name = "콘서트가 없는경우 (concertId <= 0)", value = "{ \"message\" : \"콘서트가 존재하지 않습니다. concertId : 0\"}")
                ]
            )]
        ),

    ])

    @GetMapping("/{concertId}/details")
    fun getConcertDetail(@PathVariable("concertId") concertId: Long,
                         @RequestHeader(name = "token") token: String,
                         @RequestParam(name = "reservationDateTime") reservationDateTime: LocalDateTime): List<ConcertDto.DetailResponse> {

        return concertFacade.getAllAvailableReservationDetail(
            token = token,
            concertId = concertId,
            reservationDateTime = reservationDateTime
        )
    }

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation = ConcertDto.SeatResponse::class)))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 InProgress 상태가 아닌 경우 (token == notInProgressToken)", value = "{ \"message\" : \"토큰 상태가 올바르지 않습니다.\"}"),
                ]

            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우 (token == token)", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                    ExampleObject(name = "콘서트 상세가 없는경우 (concertDetailId <= 0)", value = "{ \"message\" : \"콘서트 상세가 존재하지 않습니다. concertDetailId : 0\"}")
                ]
            )]
        ),
    ])

    @GetMapping("/details/{concertDetailId}/seats")
    fun getConcertSeat(@PathVariable("concertDetailId") concertDetailId: Long,
                       @RequestHeader(name = "token") token: String): List<ConcertDto.SeatResponse> {

        return concertFacade.getAllAvailableReservationSeat(token = token, concertDetailId = concertDetailId)
    }

    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = ConcertDto.SeatResponse::class))]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 InProgress 상태가 아닌 경우 (token == notInProgressToken)", value = "{ \"message\" : \"토큰 상태가 올바르지 않습니다.\"}"),
                    ExampleObject(name = "콘서트 예매기간이 아닌 경우 (concertSeatId == 10)", value = "{ \"message\" : \"콘서트 예매 기간이 아닙니다.\"}"),
                    ExampleObject(name = "좌석이 이미 예약된 경우 (concertSeatId == 100)", value = "{ \"message\" : \"이미 예약된 좌석입니다.\"}"),
                    ExampleObject(name = "concertSeat 의 userId와 일치하지 않는 경우 (concertSeatId != userId)", value = "{ \"message\" : \"유저Id가 일치하지 않습니다. userId : 1, concertSeatId.userId : 2\"}"),
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우 (token == token)", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                    ExampleObject(name = "콘서트 좌석이 없는 경우 (concertSeatId <= 0)", value = "{ \"message\" : \"콘서트 좌석이 존재하지 않습니다. concertSeatId : 0\"}"),

                ]
            )]
        ),

    ])

    @PostMapping("/details/seats/reservation")
    fun reservationSeat(
        @RequestHeader(name = "token") token: String,
        @RequestBody request: ConcertDto.ReservationSeatRequest
    ): ConcertDto.SeatResponse {
        return concertFacade.reserveSeatToTemporary(
            token = token,
            concertSeatId = request.concertSeatId
        )
    }


    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200", description = "성공",
            content = [Content(mediaType = "application/json")]
        ),
        ApiResponse(
            responseCode = "400", description = "요청 데이터가 잘못된 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 InProgress 상태가 아닌 경우 (token == notInProgressToken)", value = "{ \"message\" : \"토큰 상태가 올바르지 않습니다.\"}"),
                    ExampleObject(name = "임시 예약된 좌석이 아닌 경우 (concertSeatId == 10)", value = "{ \"message\" : \"임시 예약된 좌석이 아닙니다.\"}"),
                    ExampleObject(name = "concertSeat 의 userId와 일치하지 않는 경우 (concertSeatId == 100L)", value = "{ \"message\" : \"유저Id가 일치하지 않습니다. userId : 1, concertSeatId.userId : 2\"}"),
                ]
            )]
        ),
        ApiResponse(
            responseCode = "404", description = "리소스가 없는 경우",
            content = [Content(
                mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class),
                examples = [
                    ExampleObject(name = "토큰이 존재하지 않는 경우 (token == token)", value = "{ \"message\" : \"토큰이 존재하지 않습니다. token : token\"}"),
                    ExampleObject(name = "콘서트 좌석이 없는 경우 (concertSeatId <= 0)", value = "{ \"message\" : \"콘서트 좌석이 존재하지 않습니다. concertSeatId : 0\"}"),

                ]
            )]
        ),

    ])

    @PostMapping("/details/seats/{concertSeatId}/payment")
    fun paymentSeat(
        @RequestHeader(name = "token") token: String,
        @PathVariable("concertSeatId") concertSeatId: Long,
    ) {
        concertFacade.payForTemporaryReservedSeatToConfirmedReservation(
            token = token,
            concertSeatId = concertSeatId,
        )
    }
}