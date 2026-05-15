
---
name: kotlin-spring-integration-tests
description: Provides guidance on how to proceed with writing unit tests for Kotlin + Spring applications.
---

# Kotlin and Spring Unit Tests Skill

This skill provides guidelines and patterns for writing unit tests for Kotlin + Spring applications.
Use it when you need to test logic that does not depend heavily on the Spring Framework, like business logic or utility 
extension functions

## Guidelines
- Use unit tests to test behaviors that do not depend on the Spring Framework, for example:
  - A request object being converted into an entity;
  - A generic extension function that might be used in many places;
  - A business logic in the entity class;