package br.com.code.design.practice.marketplace.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.security.crypto.password.PasswordEncoder

data class UserRequest(
    @field:NotBlank
    @field:Email
    val login: String,

    @field:NotBlank
    @field:Size(min = 6)
    val password: String
) {
    fun toModel(passwordEncoder: PasswordEncoder): User {
        val encodedPassword = passwordEncoder.encode(this.password)
            ?: throw IllegalStateException("Password encoding failed")
        return User(
            login = this.login,
            password = encodedPassword
        )
    }
}
