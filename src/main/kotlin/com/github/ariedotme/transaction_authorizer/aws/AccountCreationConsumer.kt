package com.github.ariedotme.transaction_authorizer.aws

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ariedotme.transaction_authorizer.domain.Account
import com.github.ariedotme.transaction_authorizer.repository.AccountRepository
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Component
class AccountCreationConsumer(
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @SqsListener("conta-bancaria-criada")
    fun receiveMessage(@Payload messageContent: String) {
        try {
            log.debug("Nova mensagem recebida da fila: {}", messageContent)

            val message = objectMapper.readValue(messageContent, AccountCreationMessage::class.java)
            val payload = message.account

            val newAccount = Account(
                id = UUID.fromString(payload.id),
                ownerId = UUID.fromString(payload.owner),
                createdAt = Instant.ofEpochSecond(payload.createdAt),
                balance = BigDecimal.ZERO
            )

            accountRepository.save(newAccount)
            log.debug("Nova conta salva com sucesso. ID: {}", newAccount.id)

        } catch (e: Exception) {
            log.error("Erro ao processar mensagem SQS: ${e.message}", e)
        }
    }
}