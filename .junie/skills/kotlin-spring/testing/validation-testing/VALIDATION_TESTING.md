---
name: kotlin-spring-validation-tests
description: Provides guidance on how to proceed with testing the validations your spring boot application does on data it receives.
---

# Kotlin and Spring Validation Tests Skill

Use this skill when you need to write validation tests for a Kotlin + Spring application. Should be used to
all validations that an application needs to do in its borders to make sure data integrity is maintained.

## Guidelines

- To test validations on input that comes from a controller, use Spring Boot's test slice tool (`@WebMvcTest`);
  - Use it so it loads only the web layer of your Spring application;
- Make it very clear in the test class name the validation of what feature it is testing, e.g. `RegisterProductValidationTest`,
to refer to the validation of the registration of a product;

## Examples

### Controller input validation example for the @NotBlank bean validation annotation

Every validation expected to be applied by this annotation is aggregated in one unique test using a list of invalid inputs,
the expected error message is being validated as well (For validation error treatment see the error handling skill). 

```kotlin
@Test
fun `should validate if the country name is not blank`() {
    val invalidCountryNamesRequest = listOf(
        CountryRequest(name = ""),
        CountryRequest(name = " "),
        CountryRequest(name = null),
    )

    invalidCountryNamesRequest.forEach {
        mockMvc.post(COUNTRIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = it.writeAsJson()
        }.andExpect {
            status { isBadRequest()}
            content {
                contentType(MediaType.APPLICATION_JSON)
                json("""{"invalidProperties":["name"],"errorMessages":["must not be blank"]}""")
            }
        }.andDo { print() }
    }
}
```
