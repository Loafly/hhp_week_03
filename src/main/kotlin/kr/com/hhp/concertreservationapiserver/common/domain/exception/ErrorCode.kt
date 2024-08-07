package kr.com.hhp.concertreservationapiserver.common.domain.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val code: String, val message: String) {

    // 콘서트
        //400
    CONCERT_RESERVATION_PERIOD_EARLY(HttpStatus.BAD_REQUEST, "C-B-0001", "콘서트 예약 기간이 아닙니다."),
    CONCERT_RESERVATION_PERIOD_LATE(HttpStatus.BAD_REQUEST, "C-B-0002", "콘서트 예약 기간이 아닙니다."),
    CONCERT_SEAT_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "C-B-0003", "이미 예약된 콘서트 좌석입니다."),
    CONCERT_SEAT_IS_NOT_TEMPORARY_STATUS(HttpStatus.BAD_REQUEST, "C-B-0004", "임시 예약된 좌석이 아닙니다."),
    CONCERT_USER_ID_IS_MIS_MATCH(HttpStatus.BAD_REQUEST, "C-B-0005", "임시 예약한 사용자가 아닙니다."),

        //404
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "C-N-0001", "콘서트가 존재하지 않습니다."),
    CONCERT_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "C-N-0002", "콘서트 상세가 존재하지 않습니다."),
    CONCERT_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "C-N-0003", "콘서트 좌석이 존재하지 않습니다."),


    // 토큰
        //400
    TOKEN_IS_NULL(HttpStatus.BAD_REQUEST, "T-B-0001", "토큰값이 null 입니다."),
    TOKEN_STATUS_IS_NOT_PROGRESS(HttpStatus.BAD_REQUEST, "T-B-0002", "토큰 상태가 유효하지 않습니다."),

        //404
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "T-N-0001", "토큰이 존재하지 않습니다."),

    // 사용자
        //404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-N-0001", "사용자가 존재하지 않습니다."),


    // 지갑
        //400
    WALLET_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "W-B-0001", "충전/사용 금액은 양수여야 합니다."),
    WALLET_USER_ID_IS_MIS_MATCH(HttpStatus.BAD_REQUEST, "W-B-0002", "지갑 주인이 아닙니다."),
    WALLET_INVALID_BALANCE(HttpStatus.BAD_REQUEST, "W-B-0004", "잔여 금액보다 사용금액이 더 큽니다"),


        //404
    WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "W-N-0001", "지갑이 존재하지 않습니다.");

    override fun toString(): String {
        return this.message
    }
}