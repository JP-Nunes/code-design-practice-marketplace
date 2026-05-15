package br.com.code.design.practice.marketplace.user.register

import br.com.code.design.practice.marketplace.user.UserController
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(UserController::class)
class RegisterUserRequestValidationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    inner class LoginField {

        @Test
        fun `should validate if the login is not blank`() {
            val invalidLoginRequests = listOf(
                """{"login": "", "password": "password123"}""",
                """{"login": " ", "password": "password123"}""",
                """{"login": null, "password": "password123"}"""
            )

            invalidLoginRequests.forEach {
                mockMvc.post("/users") {
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = it
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        json(
                            """
                            {
                                "errors": [
                                    {
                                        "field": "login",
                                        "message": "must not be blank"
                                    }
                                ]
                            }
                            """.trimIndent()
                        )
                    }
                }
            }
        }

        @Test
        fun `should validate if the login is a well-formed email`() {
            val requestWithInvalidEmail = """
                {
                    "login": "invalid-email",
                    "password": "password123"
                }
            """.trimIndent()

            mockMvc.post("/users") {
                with(csrf())
                contentType = MediaType.APPLICATION_JSON
                content = requestWithInvalidEmail
            }.andExpect {
                status { isBadRequest() }
                content {
                    json(
                        """
                        {
                            "errors": [
                                {
                                    "field": "login",
                                    "message": "must be a well-formed email address"
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                }
            }
        }
    }

    @Nested
    inner class PasswordField {

        @Test
        fun `should validate if the password is not blank`() {
            val invalidPasswordRequests = listOf(
                """{"login": "user@example.com", "password": ""}""",
                """{"login": "user@example.com", "password": " "}""",
                """{"login": "user@example.com", "password": null}"""
            )

            invalidPasswordRequests.forEach {
                mockMvc.post("/users") {
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = it
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        jsonPath("$.errors[?(@.field == 'password' && @.message == 'must not be blank')]") { exists() }
                    }
                }
            }
        }

        @Test
        fun `should validate if the password has minimum size`() {
            val requestWithShortPassword = """
                {
                    "login": "user@example.com",
                    "password": "123"
                }
            """.trimIndent()

            mockMvc.post("/users") {
                with(csrf())
                contentType = MediaType.APPLICATION_JSON
                content = requestWithShortPassword
            }.andExpect {
                status { isBadRequest() }
                content {
                    json(
                        """
                        {
                            "errors": [
                                {
                                    "field": "password",
                                    "message": "size must be between 6 and 2147483647"
                                }
                            ]
                        }
                        """.trimIndent()
                    )
                }
            }
        }
    }
}
