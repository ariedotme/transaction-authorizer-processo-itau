package com.github.ariedotme.transaction_authorizer.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "accounts")
data class Account(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val ownerId: UUID,
    @Column(nullable = false, updatable = false)
    val createdAt: Instant,
    @Column(nullable = false)
    var balance: BigDecimal
)
