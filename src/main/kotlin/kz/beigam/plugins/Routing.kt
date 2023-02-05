package kz.beigam.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kz.beigam.data.UserDataSource
import kz.beigam.routes.getSecretInfo
import kz.beigam.routes.signIn
import kz.beigam.routes.signUp
import kz.beigam.security.hashing.HashingService
import kz.beigam.security.token.JwtTokenService
import kz.beigam.security.token.TokenConfig

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {

    routing {
        signUp(hashingService = hashingService, userDataSource = userDataSource)
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        getSecretInfo()
    }
}
