package com.github.ariedotme.transaction_authorizer.service.impl

import com.github.ariedotme.transaction_authorizer.dto.*
import com.github.ariedotme.transaction_authorizer.exception.AccountNotFoundException
import com.github.ariedotme.transaction_authorizer.repository.AccountRepository
import com.github.ariedotme.transaction_authorizer.service.AccountService
import com.github.ariedotme.transaction_authorizer.service.TransactionService
import io.micrometer.core.instrument.MeterRegistry
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@Service
class TransactionServiceImpl(
    private val accountService: AccountService,
    private val accountRepository: AccountRepository,
    private val meterRegistry: MeterRegistry
) : TransactionService {

    @Transactional
    override fun processTransaction(transactionId: UUID, accountId: UUID, request: TransactionRequest): TransactionResponse {
        val account = accountService.findById(accountId)
            ?: throw AccountNotFoundException(accountId)

        val newBalance: BigDecimal
        val status: TransactionStatus

        when (request.type) {
            TransactionType.CREDIT -> {
                newBalance = account.balance.add(request.amount.value)
                account.balance = newBalance
                accountRepository.save(account)
                status = TransactionStatus.SUCCEEDED
            }
            TransactionType.DEBIT -> {
                if (account.balance >= request.amount.value) {
                    newBalance = account.balance.subtract(request.amount.value)
                    account.balance = newBalance
                    accountRepository.save(account)
                    status = TransactionStatus.SUCCEEDED
                } else {
                    newBalance = account.balance
                    status = TransactionStatus.FAILED
                }
            }
        }

        meterRegistry.counter("transaction.processed", "type", request.type.name, "status", status.name).increment()

        return TransactionResponse(
            transaction = TransactionResponse.TransactionData(
                id = transactionId,
                type = request.type,
                amount = request.amount,
                status = status,
                timestamp = OffsetDateTime.now()
            ),
            account = TransactionResponse.AccountData(
                id = account.id,
                balance = AmountDTO(value = newBalance)
            )
        )
    }
}