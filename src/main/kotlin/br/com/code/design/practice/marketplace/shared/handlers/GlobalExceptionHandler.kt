package br.com.code.design.practice.marketplace.shared.handlers

import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map {
            FieldErrorResponse(field = it.field, message = it.defaultMessage)
        }

        return ResponseEntity.badRequest().body(ValidationErrorResponse(errors))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ValidationErrorResponse> {
        return ResponseEntity.badRequest().body(
            ValidationErrorResponse(
                errors = listOf(FieldErrorResponse(field = null, message = ex.message))
            )
        )
    }
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ValidationErrorResponse> {
        return ResponseEntity.badRequest().body(
            ValidationErrorResponse(
                errors = listOf(FieldErrorResponse(field = null, message = "Invalid request body: ${ex.message}"))
            )
        )
    }
}
