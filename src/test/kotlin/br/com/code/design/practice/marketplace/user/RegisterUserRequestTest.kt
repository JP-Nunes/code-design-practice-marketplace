package br.com.code.design.practice.marketplace.user

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterUserRequestTest {

    private val passwordEncoder = mockk<PasswordEncoder>()

    @Test
    fun `should be able to convert to User entity correctly`() {
        val request = RegisterUserRequest("user@example.com", "password123")

        val encodedPassword = "encodedPassword"
        every { passwordEncoder.encode("password123") } returns encodedPassword

        val expected = User(
            login = request.login,
            password = encodedPassword
        )
        val actual = request.toEntity(passwordEncoder)

        assertEquals(expected, actual)
    }
}
