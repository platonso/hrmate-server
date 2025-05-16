package com.platonso.data.user

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val name: String,
    val surname: String,
    val email: String,
    val position: String,
    val password: String,
    @BsonId val id: ObjectId = ObjectId()
)