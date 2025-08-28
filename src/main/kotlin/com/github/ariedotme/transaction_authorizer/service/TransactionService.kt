package com.github.ariedotme.transaction_authorizer.service

import com.github.ariedotme.transaction_authorizer.dto.TransactionRequest
import com.github.ariedotme.transaction_authorizer.dto.TransactionResponse
import java.util.*

interface TransactionService {
    fun processTransaction(transactionId: UUID, accountId: UUID, request: TransactionRequest): TransactionResponse
}