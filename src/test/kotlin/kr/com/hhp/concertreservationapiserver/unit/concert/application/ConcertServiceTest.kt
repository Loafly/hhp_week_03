package kr.com.hhp.concertreservationapiserver.unit.concert.application

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.concert.domain.repository.ConcertRepository
import kr.com.hhp.concertreservationapiserver.concert.domain.service.ConcertService
import kr.com.hhp.concertreservationapiserver.concert.infra.entity.ConcertEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.given
import org.mockito.kotlin.then

@ExtendWith(MockitoExtension::class)
class ConcertServiceTest {

    @Mock
    private lateinit var concertRepository: ConcertRepository

    @InjectMocks
    private lateinit var concertService: ConcertService

    @Nested
    @DisplayName("콘서트 조회")
    inner class GetByConcertIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            //given
            val concertId = 1L
            val expectedConcert = ConcertEntity(concertId)
            given(concertRepository.findByConcertId(concertId)).willReturn(expectedConcert)

            //when
            val concert = concertService.getByConcertId(concertId)

            //then
            then(concertRepository).should().findByConcertId(concertId)
            assertNotNull(concert)
            assertEquals(expectedConcert.concertId, concert.concertId)
        }

        @Test
        fun `실패 (콘서트가 존재하지 않는 경우)`() {
            //given
            val concertId = 1L
            val expectedConcert = ConcertEntity(concertId)
            given(concertRepository.findByConcertId(concertId)).willReturn(null)

            //when
            val exception = assertThrows<CustomException>{
                concertService.getByConcertId(concertId)
            }

            //then
            then(concertRepository).should().findByConcertId(concertId)
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.CONCERT_NOT_FOUND.code, exception.code)
        }
    }


}