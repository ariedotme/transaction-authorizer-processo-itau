package com.github.ariedotme.transaction_authorizer.service.impl

import com.github.ariedotme.transaction_authorizer.domain.Account
import com.github.ariedotme.transaction_authorizer.repository.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class AccountServiceImplTest {

    @MockK
    private lateinit var accountRepository: AccountRepository

    @InjectMockKs
    private lateinit var accountService: AccountServiceImpl

    @Test
    fun `findById deve retornar uma conta quando ela existir`() {
        val accountId = UUID.randomUUID()
        val expectedAccount = Account(
            id = accountId,
            ownerId = UUID.randomUUID(),
            createdAt = Instant.now(),
            balance = BigDecimal.TEN
        )

        every { accountRepository.findById(accountId) } returns Optional.of(expectedAccount)

        val foundAccount = accountService.findById(accountId)

        assertNotNull(foundAccount)
        assertEquals(expectedAccount.id, foundAccount.id)
        verify(exactly = 1) { accountRepository.findById(accountId) }
    }

    @Test
    fun `findById deve retornar null quando a conta não existir`() {
        val accountId = UUID.randomUUID()
        every { accountRepository.findById(accountId) } returns Optional.empty()

        val foundAccount = accountService.findById(accountId)

        assertNull(foundAccount)
        verify(exactly = 1) { accountRepository.findById(accountId) }
    }

    @Test
    fun `existsById deve retornar true quando a conta existir`() {
        val accountId = UUID.randomUUID()
        every { accountRepository.existsById(accountId) } returns true

        val exists = accountService.existsById(accountId)

        assertTrue(exists)
        verify(exactly = 1) { accountRepository.existsById(accountId) }
    }

    @Test
    fun `existsById deve retornar false quando a conta não existir`() {
        val accountId = UUID.randomUUID()
        every { accountRepository.existsById(accountId) } returns false

        val exists = accountService.existsById(accountId)

        assertFalse(exists)
        verify(exactly = 1) { accountRepository.existsById(accountId) }
    }
}