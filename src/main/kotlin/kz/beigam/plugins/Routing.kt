package kz.beigam.plugins

import io.ktor.server.routing.*
import io.ktor.server.application.*
import kz.beigam.data.source.CompanyDataSource
import kz.beigam.data.source.UserDataSource
import kz.beigam.routes.*
import kz.beigam.security.hashing.HashingService
import kz.beigam.security.token.JwtTokenService
import kz.beigam.security.token.TokenConfig
import kz.beigam.service.UserService

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig,
    userService: UserService,
    companyDataSource: CompanyDataSource
) {

    routing {
        signUp(userService)
        signIn(userDataSource, hashingService, tokenService, tokenConfig)
        getSecretInfo()
        testRoutes(userDataSource)
        companies(companyDataSource)
        users(userDataSource)
    }
}
