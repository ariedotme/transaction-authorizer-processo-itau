package com.github.ariedotme.transaction_authorizer.service

import com.github.ariedotme.transaction_authorizer.domain.Account
import java.util.UUID

interface AccountService {
    fun findById(id: UUID): Account?
    fun existsById(id: UUID): Boolean
}