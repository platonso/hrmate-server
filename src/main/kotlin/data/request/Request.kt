package com.platonso.data.request

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class Request(
    val _id: ObjectId = ObjectId(),
    val userId: ObjectId,
    val date: LocalDateTime = LocalDateTime.now(),
    val content: String,
    val status: RequestStatus = RequestStatus.PENDING
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}