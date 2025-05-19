package com.platonso

import com.platonso.data.requests.SignInRequest
import com.platonso.data.requests.SignUpRequest
import com.platonso.data.responses.AuthResponse
import com.platonso.data.responses.UserInfoResponse
import com.platonso.data.user.User
import com.platonso.data.user.UserDataSource
import com.platonso.security.token.TokenClaim
import com.platonso.security.token.TokenConfig
import com.platonso.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

fun Route.signUp(
    userDataSource: UserDataSource
) {
    post("signup") {
        val request = call.receiveNullable<SignUpRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.name.isBlank() ||
                request.surname.isBlank() ||
                request.email.isBlank() ||
                request.position.isBlank() ||
                request.password.isBlank()
        if (areFieldsBlank) {
            call.respond(HttpStatusCode.Conflict, "Не все поля заполнены")
            return@post
        }

        val isPwTooShort = request.password.length < 6
        if (isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, "Пароль должен содержать минимум 6 символов")
            return@post
        }

        if (!request.email.isValidEmail()) {
            call.respond(HttpStatusCode.Conflict, "Неверный формат почты")
            return@post
        }

        val existingUser = userDataSource.getUserByEmail(request.email)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "Пользователь с таким email уже существует")
            return@post
        }

        val user = User(
            name = request.name,
            surname = request.surname,
            email = request.email,
            position = request.position,
            password = request.password
        )
        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged) {
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        call.respond(HttpStatusCode.OK)
    }
}

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))
}


fun Route.signIn(
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = call.receiveNullable<SignInRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByEmail(request.email)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Пользователь не зарегистрирован")
            return@post
        }

        val isValidPassword = request.password == user.password
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Неверный пароль")
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )
    }
}

fun Route.authenticate() {
    authenticate {
        get("authenticate") {
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Ваш userId: $userId")
        }
    }
}

fun Route.getUserData(userDataSource: UserDataSource) {
    authenticate {
        get("user") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "Пользователь не авторизован")
                return@get
            }

            val user = userDataSource.getUserById(ObjectId(userId))
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
                return@get
            }

            val userInfoResponse = UserInfoResponse(
                id = user.id.toString(),
                name = user.name,
                surname = user.surname,
                email = user.email,
                position = user.position
            )

            call.respond(HttpStatusCode.OK, userInfoResponse)
        }
    }
}