
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
- If mocks are necessary, use mockk;
