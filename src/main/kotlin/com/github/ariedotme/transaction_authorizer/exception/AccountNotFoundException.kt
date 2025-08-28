package com.github.ariedotme.transaction_authorizer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.UUID

@ResponseStatus(HttpStatus.NOT_FOUND)
class AccountNotFoundException(val accountId: UUID) : RuntimeException()