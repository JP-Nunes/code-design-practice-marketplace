package br.com.code.design.practice.marketplace.user

import br.com.code.design.practice.marketplace.shared.handlers.GlobalExceptionHandler
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler::class)
class UserRequestValidationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    @WithMockUser
    fun `should validate login field`() {
        val invalidRequests = listOf(
            """{"login": "", "password": "password123"}""" to listOf("must not be blank"),
            """{"login": "invalid-email", "password": "password123"}""" to listOf("must be a well-formed email address")
        )

        invalidRequests.forEach { (jsonContent, expectedMessages) ->
            mockMvc.post("/users") {
                contentType = MediaType.APPLICATION_JSON
                content = jsonContent
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.errors[*].field") { value(hasItem("login")) }
                    jsonPath("$.errors[*].message") { value(containsInAnyOrder(*expectedMessages.toTypedArray())) }
                }
            }
        }

        // Test null login (HttpMessageNotReadableException)
        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"login": null, "password": "password123"}"""
        }.andExpect {
            status { isBadRequest() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.errors[*].message") { value(hasItem(startsWith("Invalid request body"))) }
            }
        }
    }

    @Test
    @WithMockUser
    fun `should validate password field`() {
        val invalidRequests = listOf(
            """{"login": "user@example.com", "password": ""}""" to listOf("must not be blank", "size must be between 6 and 2147483647"),
            """{"login": "user@example.com", "password": "123"}""" to listOf("size must be between 6 and 2147483647")
        )

        invalidRequests.forEach { (jsonContent, expectedMessages) ->
            mockMvc.post("/users") {
                contentType = MediaType.APPLICATION_JSON
                content = jsonContent
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.errors[*].field") { value(hasItem("password")) }
                    jsonPath("$.errors[*].message") { value(containsInAnyOrder(*expectedMessages.toTypedArray())) }
                }
            }
        }

        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"login": "user@example.com", "password": null}"""
        }.andExpect {
            status { isBadRequest() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.errors[*].message") { value(hasItem(startsWith("Invalid request body"))) }
            }
        }
    }
}
