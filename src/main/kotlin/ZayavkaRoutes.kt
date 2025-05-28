package com.platonso

import com.platonso.data.zayavka.Zayavka
import com.platonso.data.zayavka.ZayavkaDataSource
import com.platonso.data.requests.CreateZayavkaRequest
import com.platonso.data.requests.UpdateZayavkaStatusRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.createZayavka(
    zayavkaDataSource: ZayavkaDataSource
) {
    authenticate {
        post("zayavka/create") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@post
            }

            // Получаем JSON данные
            val createdRequest = call.receiveNullable<CreateZayavkaRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Неверный формат запроса")
                return@post
            }

            val zayavka = Zayavka(
                userId = ObjectId(userId),
                title = createdRequest.title,
                date = createdRequest.date,
                description = createdRequest.description,
                startDate = createdRequest.startDate,
                endDate = createdRequest.endDate,
                newName = createdRequest.newName,
                newSurname = createdRequest.newSurname
            )

            val wasAcknowledged = zayavkaDataSource.createZayavka(zayavka)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.InternalServerError, "Не удалось создать заявку")
                return@post
            }

            call.respond(HttpStatusCode.Created)
        }
    }
}

fun Route.getUserZayavki(zayavkaDataSource: ZayavkaDataSource) {
    authenticate {
        get("zayavki") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@get
            }

            val zayavki = zayavkaDataSource.getZayavkaByUserId(ObjectId(userId))
            call.respond(HttpStatusCode.OK, zayavki)
        }
    }
}

fun Route.getZayavkaById(zayavkaDataSource: ZayavkaDataSource) {
    authenticate {
        get("zayavka/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@get
            }

            val zayavkaId = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "ID заявки не указан")
                return@get
            }

            val zayavka = zayavkaDataSource.getZayavkaById(ObjectId(zayavkaId))
            if (zayavka == null) {
                call.respond(HttpStatusCode.NotFound, "Заявка не найдена")
                return@get
            }

            if (zayavka.userId != ObjectId(userId)) {
                call.respond(HttpStatusCode.Forbidden, "Нет доступа к этой заявке")
                return@get
            }

            call.respond(HttpStatusCode.OK, zayavka)
        }
    }
}

fun Route.getManagerUsersZayavki(zayavkaDataSource: ZayavkaDataSource) {
    get("manager/zayavki") {
        val zayavki = zayavkaDataSource.getAllZayavki()
        call.respond(HttpStatusCode.OK, zayavki)
    }
}

fun Route.updateManagerZayavkaStatus(zayavkaDataSource: ZayavkaDataSource) {
    patch("manager/zayavka/{id}/status") {
        val zayavkaId = call.parameters["id"] ?: run {
            call.respond(HttpStatusCode.BadRequest, "ID заявки не указан")
            return@patch
        }

        val request = call.receiveNullable<UpdateZayavkaStatusRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest, "Неверный формат запроса")
            return@patch
        }

        val zayavka = zayavkaDataSource.getZayavkaById(ObjectId(zayavkaId))
        if (zayavka == null) {
            call.respond(HttpStatusCode.NotFound, "Заявка не найдена")
            return@patch
        }

        val wasUpdated = zayavkaDataSource.updateZayavkaStatus(ObjectId(zayavkaId), request.status)
        if (!wasUpdated) {
            call.respond(HttpStatusCode.InternalServerError, "Не удалось обновить статус заявки")
            return@patch
        }

        call.respond(HttpStatusCode.OK)
    }
}

fun Route.deleteManagerZayavka(zayavkaDataSource: ZayavkaDataSource) {
    delete("manager/zayavka/delete/{id}") {
        val zayavkaId = call.parameters["id"] ?: run {
            call.respond(HttpStatusCode.BadRequest, "ID заявки не указан")
            return@delete
        }

        // Проверяем существование заявки
        val zayavka = zayavkaDataSource.getZayavkaById(ObjectId(zayavkaId))
        if (zayavka == null) {
            call.respond(HttpStatusCode.NotFound, "Заявка не найдена")
            return@delete
        }

        val wasDeleted = zayavkaDataSource.deleteZayavka(ObjectId(zayavkaId))
        if (!wasDeleted) {
            call.respond(HttpStatusCode.InternalServerError, "Не удалось удалить заявку")
            return@delete
        }

        call.respond(HttpStatusCode.OK)
    }
}