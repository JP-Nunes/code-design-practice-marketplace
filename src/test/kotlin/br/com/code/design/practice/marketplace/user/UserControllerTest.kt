package br.com.code.design.practice.marketplace.user

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import java.util.ArrayList

@SpringBootTest
class UserControllerTest {

    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `should create a new user when request is valid`() {
        val requestJson = "{\"login\": \"user@example.com\", \"password\": \"password123\"}"

        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated)

        val users = userRepository.findAll().iterator()
        val userList = ArrayList<User>()
        while (users.hasNext()) {
            userList.add(users.next())
        }

        assertEquals(1, userList.size)
        val user = userList.get(0)
        assertEquals("user@example.com", user.login)
        assertNotNull(user.password)
        assertNotNull(user.createdAt)
    }
}
