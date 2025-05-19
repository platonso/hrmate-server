package com.platonso.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val position: String
) 