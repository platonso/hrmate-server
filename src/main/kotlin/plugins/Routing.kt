package com.platonso.plugins

import com.platonso.authenticate
import com.platonso.data.request.RequestDataSource
import com.platonso.data.user.UserDataSource
import com.platonso.getSecretInfo
import com.platonso.getUserData
import com.platonso.security.token.TokenConfig
import com.platonso.security.token.TokenService
import com.platonso.signIn
import com.platonso.signUp
import com.platonso.createRequest
import com.platonso.getUserRequests
import com.platonso.getRequestById
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    requestDataSource: RequestDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource, tokenService, tokenConfig)
        signUp(userDataSource)
        authenticate()
        getSecretInfo()
        getUserData(userDataSource)
        
        // Маршруты для работы с заявками
        createRequest(requestDataSource)
        getUserRequests(requestDataSource)
        getRequestById(requestDataSource)
    }
}
