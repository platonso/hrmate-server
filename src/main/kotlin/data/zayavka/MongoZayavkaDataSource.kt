package com.platonso.data.zayavka

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoZayavkaDataSource(
    db: CoroutineDatabase
) : ZayavkaDataSource {
    private val requests = db.getCollection<Zayavka>("zayavka")

    override suspend fun createZayavka(zayavka: Zayavka): Boolean {
        return requests.insertOne(zayavka).wasAcknowledged()
    }

    override suspend fun getZayavkaById(id: ObjectId): Zayavka? {
        return requests.findOne(Filters.eq("_id", id))
    }

    override suspend fun getZayavkaByUserId(userId: ObjectId): List<Zayavka> {
        return requests.find(Filters.eq("userId", userId)).toList()
    }

    override suspend fun updateZayavkaStatus(id: ObjectId, status: ZayavkaStatus): Boolean {
        return requests.updateOne(
            Filters.eq("_id", id),
            Updates.set("status", status)
        ).wasAcknowledged()
    }

    override suspend fun deleteZayavka(id: ObjectId): Boolean {
        return requests.deleteOne(Filters.eq("_id", id)).wasAcknowledged()
    }

    override suspend fun getAllZayavki(): List<Zayavka> {
        return requests.find().toList()
    }
}