---
name: kotlin-spring-error-handling
description: Provides guidance on how to handle errors and how to build error responses.
---

# Kotlin and Spring Error Handling Skill

Use this skill when you need to build error responses for your application. Whether the root cause is internal or external failure.

## Guidelines

- Prefer using the response objects in the examples to build the error response.
- It is important that the root or source of the error is clear for the client, without exposing too many internal details of the application.
  - If the status code is 4xx, the source is already clear since it's an error from the client, it's not necessary
to specify the source in the body.
- The application should handle all custom exceptions.
- The application should handle `MethodArgumentNotValidException`, since it's the default for bean validation rules.
  - It is important that all validation errors that happened in a request are returned in the response payload, for example,
if email and password are invalid, the response should contain both errors.

## Examples

### Example of a response object that can be used for handling bean validation errors

```json
{
  "errors": [
    {
      "field": "email",
      "message": "must be a well-formed email address"
    },
    {
      "field": "password",
      "message": "must be at least 8 characters long"
    }
  ]
}
```
