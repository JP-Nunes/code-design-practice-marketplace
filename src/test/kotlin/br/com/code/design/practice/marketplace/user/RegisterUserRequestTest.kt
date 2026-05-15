package br.com.code.design.practice.marketplace.user

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class RegisterUserRequestTest {

    private val passwordEncoder = mockk<PasswordEncoder>()

    @Test
    fun `should convert to User entity correctly`() {
        val request = RegisterUserRequest("user@example.com", "password123")

        val encodedPassword = "encodedPassword"
        every { passwordEncoder.encode("password123") } returns encodedPassword

        val expected = User(
            login = "user@example.com",
            password = encodedPassword
        )
        val actual = request.toEntity(passwordEncoder)

        assertEquals(expected, actual)
    }

    @Test
    fun `should throw exception when login is null`() {
        val request = RegisterUserRequest(null, "password123")

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            request.toEntity(passwordEncoder)
        }

        assertEquals("Login cannot be null", exception.message)
    }

    @Test
    fun `should throw exception when password is null`() {
        val request = RegisterUserRequest("user@example.com", null)

        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            request.toEntity(passwordEncoder)
        }

        assertEquals("Password cannot be null", exception.message)
    }
}
