package br.com.code.design.practice.marketplace.shared.handlers

import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import tools.jackson.databind.exc.MismatchedInputException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map {
            ValidationErrorResponse.FieldErrorResponse(field = it.field, message = it.defaultMessage)
        }

        return ResponseEntity.badRequest().body(ValidationErrorResponse(errors))
    }
}
