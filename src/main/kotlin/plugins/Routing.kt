package com.platonso.plugins

import com.platonso.authenticate
import com.platonso.data.user.UserDataSource
import com.platonso.getSecretInfo
import com.platonso.getUserData
import com.platonso.security.token.TokenConfig
import com.platonso.security.token.TokenService
import com.platonso.signIn
import com.platonso.signUp
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource, tokenService, tokenConfig)
        signUp(userDataSource)
        authenticate()
        getSecretInfo()
        getUserData(userDataSource)
    }
}
