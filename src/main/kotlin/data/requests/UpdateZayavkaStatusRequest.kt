package com.platonso.data.requests

import com.platonso.data.zayavka.ZayavkaStatus
import kotlinx.serialization.Serializable

@Serializable
data class UpdateZayavkaStatusRequest(
    val status: ZayavkaStatus
)