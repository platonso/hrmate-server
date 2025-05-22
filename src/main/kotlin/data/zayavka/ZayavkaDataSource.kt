package com.platonso.data.zayavka

import org.bson.types.ObjectId

interface ZayavkaDataSource {
    suspend fun createZayavka(zayavka: Zayavka): Boolean
    suspend fun getZayavkaById(id: ObjectId): Zayavka?
    suspend fun getZayavkaByUserId(userId: ObjectId): List<Zayavka>
    suspend fun updateZayavkaStatus(id: ObjectId, status: ZayavkaStatus): Boolean
    suspend fun deleteZayavka(id: ObjectId): Boolean
}
