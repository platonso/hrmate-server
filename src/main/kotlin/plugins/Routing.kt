package com.platonso.plugins

import com.platonso.authenticate
import com.platonso.data.zayavka.ZayavkaDataSource
import com.platonso.data.user.UserDataSource
import com.platonso.getSecretInfo
import com.platonso.getUserData
import com.platonso.security.token.TokenConfig
import com.platonso.security.token.TokenService
import com.platonso.signIn
import com.platonso.signUp
import com.platonso.createZayavka
import com.platonso.deleteManagerZayavka
import com.platonso.getManagerUsersZayavki
import com.platonso.getUserZayavki
import com.platonso.getZayavkaById
import com.platonso.updateManagerZayavkaStatus
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    zayavkaDataSource: ZayavkaDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        signIn(userDataSource, tokenService, tokenConfig)
        signUp(userDataSource)
        authenticate()
        getSecretInfo()
        getUserData(userDataSource)

        createZayavka(zayavkaDataSource)
        getUserZayavki(zayavkaDataSource)
        getZayavkaById(zayavkaDataSource)

        getManagerUsersZayavki(zayavkaDataSource)
        updateManagerZayavkaStatus(zayavkaDataSource)
        deleteManagerZayavka(zayavkaDataSource)
    }
}
