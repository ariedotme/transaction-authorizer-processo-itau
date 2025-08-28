package com.github.ariedotme.transaction_authorizer.service.impl

import com.github.ariedotme.transaction_authorizer.domain.Account
import com.github.ariedotme.transaction_authorizer.dto.AmountDTO
import com.github.ariedotme.transaction_authorizer.dto.TransactionRequest
import com.github.ariedotme.transaction_authorizer.dto.TransactionStatus
import com.github.ariedotme.transaction_authorizer.dto.TransactionType
import com.github.ariedotme.transaction_authorizer.exception.AccountNotFoundException
import com.github.ariedotme.transaction_authorizer.repository.AccountRepository
import com.github.ariedotme.transaction_authorizer.service.AccountService
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class TransactionServiceImplTest {

    @MockK
    private lateinit var accountService: AccountService

    @MockK
    private lateinit var accountRepository: AccountRepository

    @MockK
    private lateinit var meterRegistry: MeterRegistry

    @InjectMockKs
    private lateinit var transactionService: TransactionServiceImpl

    @BeforeEach
    fun setUp() {
        val counterMock = mockk<Counter>(relaxed = true)
        every { meterRegistry.counter(any(), any<String>(), any<String>(), any<String>(), any<String>()) } returns counterMock
    }

    @Test
    fun `deve processar uma transação de CRÉDITO com sucesso`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val initialBalance = BigDecimal("100.00")
        val transactionValue = BigDecimal("50.00")
        val account = Account(accountId, UUID.randomUUID(), Instant.now(), initialBalance)
        val request = TransactionRequest(TransactionType.CREDIT, AmountDTO(transactionValue))

        every { accountService.findById(accountId) } returns account
        every { accountRepository.save(any()) } returnsArgument 0

        val response = transactionService.processTransaction(transactionId, accountId, request)

        assertEquals(TransactionStatus.SUCCEEDED, response.transaction.status)
        assertEquals(BigDecimal("150.00"), response.account.balance.value)
        verify(exactly = 1) { accountRepository.save(any()) }
    }

    @Test
    fun `deve processar uma transação de DÉBITO com sucesso quando há saldo suficiente`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val initialBalance = BigDecimal("100.00")
        val transactionValue = BigDecimal("70.00")
        val account = Account(accountId, UUID.randomUUID(), Instant.now(), initialBalance)
        val request = TransactionRequest(TransactionType.DEBIT, AmountDTO(transactionValue))

        every { accountService.findById(accountId) } returns account
        every { accountRepository.save(any()) } returnsArgument 0

        val response = transactionService.processTransaction(transactionId, accountId, request)

        assertEquals(TransactionStatus.SUCCEEDED, response.transaction.status)
        assertEquals(BigDecimal("30.00"), response.account.balance.value)
        verify(exactly = 1) { accountRepository.save(any()) }
    }

    @Test
    fun `deve RECUSAR uma transação de DÉBITO quando o saldo for insuficiente`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val initialBalance = BigDecimal("50.00")
        val transactionValue = BigDecimal("80.00")
        val account = Account(accountId, UUID.randomUUID(), Instant.now(), initialBalance)
        val request = TransactionRequest(TransactionType.DEBIT, AmountDTO(transactionValue))

        every { accountService.findById(accountId) } returns account

        val response = transactionService.processTransaction(transactionId, accountId, request)

        assertEquals(TransactionStatus.FAILED, response.transaction.status)
        assertEquals(initialBalance, response.account.balance.value)
        verify(exactly = 0) { accountRepository.save(any()) }
    }

    @Test
    fun `deve lançar uma exceção ao tentar transacionar em uma conta inexistente`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val request = TransactionRequest(TransactionType.CREDIT, AmountDTO(BigDecimal.TEN))

        every { accountService.findById(accountId) } returns null

        assertThrows(AccountNotFoundException::class.java) {
            transactionService.processTransaction(transactionId, accountId, request)
        }
    }
}