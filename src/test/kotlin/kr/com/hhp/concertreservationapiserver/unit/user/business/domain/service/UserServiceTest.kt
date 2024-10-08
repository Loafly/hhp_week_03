package kr.com.hhp.concertreservationapiserver.unit.user.business.domain.service

import kr.com.hhp.concertreservationapiserver.common.domain.exception.CustomException
import kr.com.hhp.concertreservationapiserver.common.domain.exception.ErrorCode
import kr.com.hhp.concertreservationapiserver.user.business.domain.repository.UserRepository
import kr.com.hhp.concertreservationapiserver.user.business.domain.service.UserService
import kr.com.hhp.concertreservationapiserver.user.business.domain.entity.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Nested
    @DisplayName("사용자 조회")
    inner class GetByUserIdTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val userId = 1L
            val expectedUser = UserEntity(userId)
            given(userRepository.findByUserId(userId)).willReturn(UserEntity(userId))

            // when
            val user = userService.getByUserId(userId)

            // then
            then(userRepository).should().findByUserId(userId)
            assertEquals(expectedUser.userId, user.userId)
        }

        @Test
        fun `실패 (사용자가 존재하지 않는 경우)`() {
            // given
            val userId = 1L
            given(userRepository.findByUserId(userId)).willReturn(null)

            // when
            val exception = assertThrows<CustomException> {
                userService.getByUserId(userId)
            }

            // then
            then(userRepository).should().findByUserId(userId)
            assertEquals(ErrorCode.USER_NOT_FOUND.message, exception.message)
            assertEquals(ErrorCode.USER_NOT_FOUND.code, exception.code)
        }
    }

    @Nested
    @DisplayName("사용자 저장")
    inner class SaveTest {
        @Test
        fun `성공 (정상 케이스)`() {
            // given
            val userId = 1L
            val expectedUser = UserEntity(userId)
            given(userRepository.save(any())).willReturn(expectedUser)

            // when
            val user = userService.save()

            // then
            then(userRepository).should().save(any())
            assertEquals(expectedUser.userId, user.userId)
        }
    }
}