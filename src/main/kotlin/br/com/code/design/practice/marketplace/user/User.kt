package br.com.code.design.practice.marketplace.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @field:NotBlank val login: String,
    @field:NotBlank val password: String,
) {
    @Id @GeneratedValue val id: UUID? = null
    val createdAt: LocalDateTime = LocalDateTime.now()
}
