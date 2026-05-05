
---
name: kotlin-spring-security
description: Provides guidance on how to work with Spring Security in Kotlin + Spring applications.
---

# Kotlin and Spring Unit Tests Skill

This skill provides guidelines and patterns to work with Spring Security in Kotlin + Spring applications.

## Guidelines

### Hashing Passwords
- At the moment of writing this, 05/05/2026, following the [Spring Security Docs](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#password4j-argon2),
using Spring Security version 7.0.5, the recommended way to hash passwords in new applications is using Argon2 implementation: "Argon2 is the winner of the Password Hashing Competition and is recommended for new applications."

## Examples

### Hashing a password with Argon2
<b>The default way</b>:
```kotlin
val encoder: PasswordEncoder = Argon2Password4jPasswordEncoder()
val result = encoder.encode("myPassword")
assertThat(encoder.matches("myPassword", result)).isTrue()
```

<b>The custom way</b>: 
```kotlin
val argon2Fn = Argon2Function.getInstance(
    65536, 3, 4, 32,
    Argon2.ID
)
val encoder: PasswordEncoder = Argon2Password4jPasswordEncoder(argon2Fn)
val result = encoder.encode("myPassword")
assertThat(encoder.matches("myPassword", result)).isTrue()`
```
