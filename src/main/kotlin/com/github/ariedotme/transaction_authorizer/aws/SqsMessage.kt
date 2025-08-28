package com.github.ariedotme.transaction_authorizer.aws

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountCreationMessage(
    @JsonProperty("account")
    val account: AccountPayload
)

data class AccountPayload(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("owner")
    val owner: String,
    @JsonProperty("created_at")
    val createdAt: Long,
    @JsonProperty("status")
    val status: String
)