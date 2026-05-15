package br.com.code.design.practice.marketplace.user.register

import br.com.code.design.practice.marketplace.user.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class RegisterUserIntegrationTests {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var passwordEncoder: PasswordEncoder

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun `should be able to register a new user`() {
        val registerUserRequest = """
            {
              "login": "user@example.com",
              "password": "password123"
            }
        """.trimIndent()

        mockMvc.post("/users") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = registerUserRequest
        }.andExpect {
            status { isCreated() }
        }

        val users = userRepository.findAll().toList().also { assertEquals(1, it.size) }
        users[0].run {
            assertEquals("user@example.com", this.login)
            assertNotNull(this.password)
            assertTrue(passwordEncoder.matches("password123", this.password))
            assertNotNull(this.createdAt)
        }
    }
}
