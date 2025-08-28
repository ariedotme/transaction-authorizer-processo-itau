package com.github.ariedotme.transaction_authorizer.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ariedotme.transaction_authorizer.dto.*
import com.github.ariedotme.transaction_authorizer.exception.AccountNotFoundException
import com.github.ariedotme.transaction_authorizer.service.TransactionService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@WebMvcTest(TransactionController::class)
class TransactionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var transactionService: TransactionService

    @Test
    fun `deve retornar 200 OK e o corpo da resposta ao processar uma transação com sucesso`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val request = TransactionRequest(TransactionType.CREDIT, AmountDTO(BigDecimal.TEN))
        val expectedResponse = TransactionResponse(
            transaction = TransactionResponse.TransactionData(
                id = transactionId,
                type = TransactionType.CREDIT,
                amount = AmountDTO(BigDecimal.TEN),
                status = TransactionStatus.SUCCEEDED,
                timestamp = OffsetDateTime.now()
            ),
            account = TransactionResponse.AccountData(
                id = accountId,
                balance = AmountDTO(BigDecimal("110.00"))
            )
        )

        whenever(transactionService.processTransaction(any(), any(), any())).thenReturn(expectedResponse)

        mockMvc.post("/api/v1/accounts/$accountId/transactions/$transactionId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.transaction.status") { value("SUCCEEDED") }
        }
    }

    @Test
    fun `deve retornar 404 Not Found quando a conta não existir`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val request = TransactionRequest(TransactionType.CREDIT, AmountDTO(BigDecimal.TEN))

        whenever(transactionService.processTransaction(any(), any(), any()))
            .thenThrow(AccountNotFoundException(accountId))

        mockMvc.post("/api/v1/accounts/$accountId/transactions/$transactionId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Account not found with ID: $accountId") }
        }
    }

    @Test
    fun `deve retornar 400 Bad Request para uma transação com valor negativo`() {
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val requestBody = """
            {
                "type": "DEBIT",
                "amount": {
                    "value": -50.00,
                    "currency": "BRL"
                }
            }
        """.trimIndent()

        mockMvc.post("/api/v1/accounts/$accountId/transactions/$transactionId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("Validation failed: 'amount.value': must be greater than 0") }
        }
    }
}