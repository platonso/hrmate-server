package com.platonso.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateZayavkaRequest(
    val date: String,
    val content: String
)
