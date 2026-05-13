
---
name: kotlin-spring-integration-tests
description: Provides guidance on how to proceed with writing unit tests for Kotlin + Spring applications.
---

# Kotlin and Spring Unit Tests Skill

This skill provides guidelines and patterns for writing unit tests for Kotlin + Spring applications.
Use it when you need to test logic that does not depend heavily on the Spring Framework, like business logic or utility 
extension functions

## Guidelines
- Use the pattern "should be able to..." for naming the test functions;
- If mocks are necessary, prefer mockk (`testImplementation("io.mockk:mockk:1.14.4")`, check for more recent versions) over mockito;
- Use unit tests to test behaviors that do not depend on the Spring Framework, for example:
  - A request object being converted into an entity;
  - A generic extension function that might be used in many places;
  - A business logic in the entity class;

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