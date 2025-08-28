package com.github.ariedotme.transaction_authorizer.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

enum class TransactionType {
    CREDIT, DEBIT
}
enum class TransactionStatus {
    SUCCEEDED, FAILED
}

data class AmountDTO(
    @field:NotNull
    @field:Positive
    val value: BigDecimal,
    val currency: String = "BRL"
)


data class TransactionRequest(
    @field:NotNull
    val type: TransactionType,
    @field:Valid
    val amount: AmountDTO
)

data class TransactionResponse(
    val transaction: TransactionData,
    val account: AccountData
) {
    data class TransactionData(
        val id: UUID,
    val type: TransactionType,
    val amount: AmountDTO,
    val status: TransactionStatus,
    val timestamp: OffsetDateTime
    )

    data class AccountData(
        val id: UUID,
    val balance: AmountDTO
    )
}