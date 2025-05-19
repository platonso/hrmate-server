package com.platonso.data.user

import org.bson.types.ObjectId

interface UserDataSource {
    suspend fun getUserByEmail(email: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun getUserById(id: ObjectId): User?
}