package com.github.ariedotme.transaction_authorizer.repository

import com.github.ariedotme.transaction_authorizer.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AccountRepository : JpaRepository<Account, UUID>