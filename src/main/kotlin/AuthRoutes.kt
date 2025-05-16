package com.platonso

import com.platonso.data.requests.SignInRequest
import com.platonso.data.requests.SignUpRequest
import com.platonso.data.responses.AuthResponse
import com.platonso.data.user.User
import com.platonso.data.user.UserDataSource
import com.platonso.security.token.TokenClaim
import com.platonso.security.token.TokenConfig
import com.platonso.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

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
        val isPwTooShort = request.password.length < 4
        if (areFieldsBlank || isPwTooShort) {
            call.respond(HttpStatusCode.Conflict, "The password must be at least 4 characters long")
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
            call.respond(HttpStatusCode.Conflict, "User not found")
            return@post
        }

        val isValidPassword = request.password == user.password
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, "Incorrect password")
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
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}