package com.platonso.data.request

import com.platonso.data.user.ObjectIdSerializer
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Request(
    @Serializable(with = ObjectIdSerializer::class)
    @BsonId val id: ObjectId = ObjectId(),
    @Serializable(with = ObjectIdSerializer::class)
    val userId: ObjectId,
    val title: String,
    val date: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val newName: String,
    val newSurname: String,
    val status: RequestStatus = RequestStatus.PENDING
)

@Serializable
enum class RequestStatus {
    PENDING,
    APPROVED,
    REJECTED
}