package com.platonso

import com.platonso.data.request.Request
import com.platonso.data.request.RequestDataSource
import com.platonso.data.requests.CreateZayavkaRequest
import io.ktor.client.request.request
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.createRequest(
    requestDataSource: RequestDataSource
) {
    authenticate {
        post("requests") {
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

            val request = Request(
                userId = ObjectId(userId),
                title = createdRequest.title,
                date = createdRequest.date,
                description = createdRequest.description,
                startDate = createdRequest.startDate,
                endDate = createdRequest.endDate,
                newName = createdRequest.newName,
                newSurname = createdRequest.newSurname
            )

            val wasAcknowledged = requestDataSource.createRequest(request)
            if (!wasAcknowledged) {
                call.respond(HttpStatusCode.InternalServerError, "Не удалось создать заявку")
                return@post
            }

            call.respond(HttpStatusCode.Created)
        }
    }
}

fun Route.getUserRequests(requestDataSource: RequestDataSource) {
    authenticate {
        get("requests") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@get
            }

            val requests = requestDataSource.getRequestsByUserId(ObjectId(userId))
            call.respond(HttpStatusCode.OK, requests)
        }
    }
}

fun Route.getRequestById(requestDataSource: RequestDataSource) {
    authenticate {
        get("requests/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class) ?: run {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@get
            }

            val requestId = call.parameters["id"] ?: run {
                call.respond(HttpStatusCode.BadRequest, "ID заявки не указан")
                return@get
            }

            val request = requestDataSource.getRequestById(ObjectId(requestId))
            if (request == null) {
                call.respond(HttpStatusCode.NotFound, "Заявка не найдена")
                return@get
            }

            if (request.userId != ObjectId(userId)) {
                call.respond(HttpStatusCode.Forbidden, "Нет доступа к этой заявке")
                return@get
            }

            call.respond(HttpStatusCode.OK, request)
        }
    }
} 