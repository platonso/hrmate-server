package com.platonso.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateZayavkaRequest(
    val title: String,
    val date: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val newName: String,
    val newSurname: String
)
