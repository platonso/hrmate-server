package com.platonso.data.request

import org.bson.types.ObjectId

interface RequestDataSource {
    suspend fun createRequest(request: Request): Boolean
    suspend fun getRequestById(id: ObjectId): Request?
    suspend fun getRequestsByUserId(userId: ObjectId): List<Request>
    suspend fun updateRequestStatus(id: ObjectId, status: RequestStatus): Boolean
    suspend fun deleteRequest(id: ObjectId): Boolean
} 