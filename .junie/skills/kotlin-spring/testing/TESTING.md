
---
name: kotlin-spring-tests
description: Provides guidance on how to proceed with writing tests for Kotlin + Spring applications.
---

# Kotlin and Spring Tests Skill

This skill provides guidelines and patterns for writing tests for Kotlin + Spring applications.
Use it when you need to do any sort of testing in your application.

## Guidelines
- Use the pattern "should be able to..." for naming the test functions;
- If mocks are necessary, prefer mockk (`testImplementation("io.mockk:mockk:1.14.4")`, check for more recent versions) over mockito;
- When the object under assertion is a value object, compare the full actual object with a full expected object instead of
comparing the properties individually;

## Examples

### Asserting equality in Value Objects

Value objects equality is defined by the values of their properties. So the assertion should be done in the whole object,
like so:

```kotlin
@Test
fun `should convert to User entity correctly`() {
    val request = UserRequest("user@example.com", "password123")

    val encodedPassword = "encodedPassword"
    every { passwordEncoder.encode("password123") } returns encodedPassword

    val expected = User(
        login = request.login,
        password = encodedPassword
    )
    val actual = request.toEntity(passwordEncoder)

    assertEquals(expected, actual)
}
```