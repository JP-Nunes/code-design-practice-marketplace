package br.com.code.design.practice.marketplace.shared.handlers

data class ValidationErrorResponse(
    val errors: List<FieldErrorResponse>
)

data class FieldErrorResponse(
    val field: String?,
    val message: String?
)
