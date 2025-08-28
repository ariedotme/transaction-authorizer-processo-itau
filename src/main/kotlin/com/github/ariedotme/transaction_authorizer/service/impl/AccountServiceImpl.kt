package com.github.ariedotme.transaction_authorizer.service.impl

import com.github.ariedotme.transaction_authorizer.domain.Account
import com.github.ariedotme.transaction_authorizer.repository.AccountRepository
import com.github.ariedotme.transaction_authorizer.service.AccountService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository
) : AccountService {

    override fun findById(id: UUID): Account? {
        return accountRepository.findById(id).orElse(null)
    }

    override fun existsById(id: UUID): Boolean {
        return accountRepository.existsById(id)
    }
}