package com.platonso.data.request

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoRequestDataSource(
    private val db: CoroutineDatabase
) : RequestDataSource {
    private val requests = db.getCollection<Request>("requests")

    override suspend fun createRequest(request: Request): Boolean {
        return requests.insertOne(request).wasAcknowledged()
    }

    override suspend fun getRequestById(id: ObjectId): Request? {
        return requests.findOne(Filters.eq("_id", id))
    }

    override suspend fun getRequestsByUserId(userId: ObjectId): List<Request> {
        return requests.find(Filters.eq("userId", userId)).toList()
    }

    override suspend fun updateRequestStatus(id: ObjectId, status: RequestStatus): Boolean {
        return requests.updateOne(
            Filters.eq("_id", id),
            Updates.set("status", status)
        ).wasAcknowledged()
    }

    override suspend fun deleteRequest(id: ObjectId): Boolean {
        return requests.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
    }
} 