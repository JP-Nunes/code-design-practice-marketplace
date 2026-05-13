---
name: kotlin-spring-integration-tests
description: Provides guidance on how to proceed with writing integration tests for Kotlin + Spring applications.
---

# Kotlin and Spring Integration Tests Skill

Use this skill when you need to write integration tests for a Kotlin + Spring application. Should be used to
all functionality that depends heavily on the framework.

## Guidelines
- Use Spring Boot's testing tools (e.g., `@SpringBootTest`);
- Prefer `@AutoConfigureMockMvc` whenever possible;
- Use `MockMvc` with Kotlin DSL for the assertions;
- Write the given request JSON and the expected response JSON as Strings;
  - If this JSON is small, you can leave inside the test function;
  - If this JSON is too big, you can declare it outside the test function but keep it next to that test function(s) that uses it.
  - For the response JSON, use it for the assertion.
- When the expected JSON has dynamic fields like an auto generated `id` or `timestamp`, create a custom JSON comparator that only checks if those fields exist.
- Create a separate directory only for the integration tests, preferably a directory called "integration" inside the test directory.
- Name the test class with the name of functionality that it tests, e.g., `RegisterUserIntegrationTests` for the tests related to registering an `User`.
- Make sure one test does not affect the other, e.g., cleaning the database after each test.

## Examples

### Example of a small JSON request and response being used inside the test function:

```kotlin
@Test
fun `should be able to register a voucher in the database and return response`() {
    val tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    
    mockMvc.post(VOUCHERS_V1_PATH) {
        contentType = MediaType.APPLICATION_JSON
        characterEncoding = "UTF-8"
        content = """
            {
                "code": "SUMMER-10",
                "discount": 10.0,
                "expirationDate": "$tomorrow"
            }
        """.trimIndent()
    }.andExpect {
        status { isCreated() }
        content { 
            json(
                jsonContent = """
                    {
                        "code": "SUMMER-10",
                        "discount": 10,
                        "expirationDate": "$tomorrow"
                    }
                """.trimIndent(),
                compareMode = JsonCompareMode.STRICT
            )
        }
    }.andDo { println() }
}
```

### Asserting equality in Value Objects

Value objects equality is defined by the values of their properties. So the assertion should be done in the whole object:

`assertEquals(expected, actual)`
