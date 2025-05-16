package com.platonso.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val surname: String,
    val email: String,
    val position: String,
    val password: String
)
