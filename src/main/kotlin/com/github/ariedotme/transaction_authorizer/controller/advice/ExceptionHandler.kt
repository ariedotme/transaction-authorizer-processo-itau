package com.github.ariedotme.transaction_authorizer.controller.advice

import com.github.ariedotme.transaction_authorizer.exception.AccountNotFoundException
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*

data class ErrorResponse(val message: String?)

@ControllerAdvice
class ExceptionHandler(
    private val messageSource: MessageSource
) {

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFound(ex: AccountNotFoundException): ResponseEntity<ErrorResponse> {
        val message = messageSource.getMessage(
            "error.account.notfound",
            arrayOf(ex.accountId),
            Locale.getDefault()
        )
        val errorResponse = ErrorResponse(message)
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") { "'${it.field}': ${it.defaultMessage}" }

        val errorResponse = ErrorResponse("Validation failed: $errors")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}