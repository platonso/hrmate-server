package com.platonso

import com.platonso.data.request.MongoRequestDataSource
import com.platonso.data.user.User
import com.platonso.data.user.MongoUserDataSource
import com.platonso.plugins.configureMonitoring
import com.platonso.plugins.configureRouting
import com.platonso.plugins.configureSecurity
import com.platonso.plugins.configureSerialization
import com.platonso.security.token.JwtTokenService
import com.platonso.security.token.TokenConfig
import io.ktor.server.application.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "hrmate-db"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://platonso:$mongoPw@cluster0.wncrhyq.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
    ).coroutine
        .getDatabase(dbName)
    val userDataSource = MongoUserDataSource(db)
    val requestDataSource = MongoRequestDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    configureSerialization()
    configureMonitoring()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, requestDataSource, tokenService, tokenConfig)
}
