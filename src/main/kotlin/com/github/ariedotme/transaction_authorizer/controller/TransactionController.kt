package com.github.ariedotme.transaction_authorizer.controller

import com.github.ariedotme.transaction_authorizer.dto.TransactionRequest
import com.github.ariedotme.transaction_authorizer.dto.TransactionResponse
import com.github.ariedotme.transaction_authorizer.service.TransactionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/")
class TransactionController(
    private val transactionService: TransactionService
) {



    @PostMapping("/accounts/{accountId}/transactions/{transactionId}")
    @Operation(summary = "Autoriza uma transação financeira (crédito ou débito)")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Transação processada com sucesso ou recusada por regra de negócio",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = TransactionResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Conta não encontrada",
            content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)]
        )
    ])
    fun authorizeTransaction(
        @PathVariable accountId: UUID,
        @PathVariable transactionId: UUID,
        @Valid @RequestBody request: TransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val response = transactionService.processTransaction(transactionId, accountId, request)
        return ResponseEntity.ok(response)
    }
}