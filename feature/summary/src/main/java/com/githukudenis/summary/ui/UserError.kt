package com.githukudenis.summary.ui

data class UserError(
    val id: Long = 0,
    val message: String? = null,
    val errorType: ErrorType = ErrorType.DEFAULT
)

enum class ErrorType {
    DEFAULT,
    CRITICAL
}