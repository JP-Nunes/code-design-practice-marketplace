
---
name: kotlin-spring-tests
description: Provides guidance on how to proceed with writing tests for Kotlin + Spring applications.
---

# Kotlin and Spring Tests Skill

This skill provides guidelines and patterns for writing tests for Kotlin + Spring applications.
Use it when you need to do any sort of testing in your application.

## Testing in general

### Guidelines
- Use the pattern "should be able to..." for naming the test functions;
- If mocks are necessary, prefer mockk (`testImplementation("io.mockk:mockk:1.14.4")`, check for more recent versions) over mockito;
- When the object under assertion is a value object, compare the full actual object with a full expected object instead of
comparing the properties individually;
- Even in testing, the variables should have meaningful names, e.g., a variable that represents a user object with an
invalid blank e-mail, should be called `userWithBlankEmail`;

### Examples

#### Asserting equality in Value Objects

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

## Integration testing

### Guidelines

- The integration tests should be organized as follows:
  1. Create a separate directory only for the integration tests, preferably a directory called "test-integration" in
  the same level as the typical "main" and "test" folders.
     - For this to work, there are adjustments you need to make to the build.gradle file.
  2. For each domain, create a separate directory inside the integration tests directory, e.g., "user" for the User domain.
  3. Inside each domain directory, there should be a separate directory for each feature of that domain, e.g., "register"
  for the registration of a User.
  4. Inside the feature folder, there should be the test classes for the integration tests of that feature. Which should be
  organized as follows:
     - The complete feature test class, that should be named `FeatureNameIntegrationTests` where `FeatureName` 
     is the name of the feature being tested, e.g., `RegisterUserIntegrationTests` for the registration of a User.
     - If there is validation on the input layer (e.g., controller). Then a test class for the validations is needed,
     which should be named `FeatureNameValidationIntegrationTests` where `FeatureName` is the name of the feature being
     tested, e.g., `RegisterProductRequestValidationTest` for the validation of the request of registering a product.
     - If there is a repository layer, then a test class for the repository layer is needed, which should be named
     `FeatureNameRepositoryIntegrationTests` where `FeatureName` is the name of the feature being tested, e.g.,
     `RegisterUserRepositoryIntegrationTests` for the registration of a User through the repository layer.
- For tests that use tools like MockMvc and deal with JSON in the request and response:
  - The request JSON should be created aiming for the effect that each test wants to reproduce:
    - If it is a success scenario case, the request JSON should be created to be a valid request.
    - If it is a failure scenario, the request JSON should be created to be an invalid request.
    - If it's necessary to assign the JSON to a variable, name the variable in a way that makes clear the purpose of
    that request object.
  - The response JSON should be created to be used as the expected value of the assertion, use the entire JSON
  for the assertion.
  - If the JSON is small, you can leave inside the test function;
  - If the JSON is too big, you can declare it outside the test function but keep it next to that test 
  function(s) that uses it.
  - Keep the JSONs formatted to make it easier for humans to read and understand.
- For tests that use MockMvc:
  - Prefer `@AutoConfigureMockMvc` whenever possible;
  - MockMvc is useful with Kotlin DSL, with a lot of nice result-matching options, try using it as much as possible;

#### Complete integration tests

Use this section to understand the guidelines on how to write specifically complete integration tests.

- Use `@SpringBootTest` to load the entire context;
- Use `MockMvc` for those tests if they are supposed to test an http endpoint;
- The main purpose here is to cover the entire flow of the application and all business rules, and the behavior expected 
of the application given the many variations of the input.
- The errors that can have through validations in the input layer (e.g., controller) should not be covered here; we have
a specific test class for that.
- When the expected JSON has dynamic fields like an auto generated `id` or `timestamp`, create a custom JSON comparator 
that only checks if those fields exist.

#### Validation integration tests

Use this section to understand the guidelines on how to write specifically validation integration tests.

- If the input validations are happening in the controller;
  - Use `@WebMvcTest` to load only the web layer; 
  - Use `MockMvc` for those tests;
- Make it clear in the test function name what validation it is testing, e.g., `should validate if the product name is 
not blank`;
- Use nesting to separate the validation tests by property of the class, e.g., if we are testing the `description` 
property of the `Product` class, then there should be a nested class for this property;
- Inside the nested class, the test functions should be organized as follows: 
  - Each test function should focus on a single validation, e.g., if the `description` property is annotated with 
  `@NotBlank`, then there should be a test focusing on that validation;
    - Every validation that is implied by a given annotation should be tested in the same test function, e.g, 
    `@NotBlank` implies `@NotEmpty` and `@NotEmpty` implies `@NotNull`;
  - The name of the test should reflect the validation it is testing, e.g., `should validate if the product description
  is not blank`;

### Examples

#### Example of the changes needed to the build.gradle file for the extra "test-integration" directory:

```kotlin
plugins {
    // ... other plugins
    idea
}

// ...

sourceSets {
    create("integrationTest") {
        kotlin { srcDir("src/test-integration/kotlin") }
        resources { srcDir("src/test-integration/resources") }

        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

idea {
    module {
        testSources.from(sourceSets["integrationTest"].java.srcDirs)
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
integrationTestImplementation.extendsFrom(configurations.testImplementation.get())

val integrationTestRuntimeOnly by configurations.getting
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

// ...

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}
```


#### Example of a small JSON request and response being used inside the test function:

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

### Controller input validation example for the @NotBlank bean validation annotation

Every validation expected to be applied by this annotation is aggregated in one unique test using a list of invalid 
inputs, the expected error message is being validated as well (For validation error treatment see the error handling 
skill).

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

## Unit testing

### Guidelines

- Use unit tests to test behaviors that do not depend on the Spring Framework, for example:
    - A request object being converted into an entity;
    - A generic extension function that might be used in many places;
    - A business logic in the entity class;