package kz.beigam.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kz.beigam.data.models.requests.AuthRequest
import kz.beigam.data.models.responses.AuthResponse
import kz.beigam.data.source.UserDataSource
import kz.beigam.exceptions.UserException
import kz.beigam.security.hashing.HashingService
import kz.beigam.security.hashing.SaltedHash
import kz.beigam.security.token.JwtTokenService
import kz.beigam.security.token.TokenClaim
import kz.beigam.security.token.TokenConfig
import kz.beigam.service.UserService

fun Route.signUp(
    userService: UserService
) {
    post("signup") {
        try {
            val request = call.receive<AuthRequest>()
            userService.createUser(request)
            call.respond(HttpStatusCode.OK)
        } catch (e: UserException) {
            call.respond(HttpStatusCode.Conflict, e.message?:"Error user creation")
            return@post
        } catch (e: ContentTransformationException) {
            call.respond(HttpStatusCode.Conflict, e.message?:"Error data")
            return@post
        }
    }
}


fun Route.signIn(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    post("signin") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest,AuthResponse(error="Incorrect request data"))
            return@post
        }
        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, AuthResponse(error="Incorrect username or password"))
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Conflict, AuthResponse(error="Incorrect username or password"))
            return@post
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


fun Route.getSecretInfo() {
    authenticate {
        get("secret") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your id: $userId")
        }
    }
}

fun Route.testRoutes(
    userDataSource: UserDataSource
) {
    route("test") {
        get("all_users") {
            val result = userDataSource.getAllUsers().map { it.toResponse() }
            call.respond(
                status = HttpStatusCode.OK,
                message = result
            )
        }

        get("user/{username}") {
            val username = call.parameters["username"]
            if (username == null) {
                call.respond(HttpStatusCode.Conflict, "Incorrect username")
                return@get
            } else {
                val user = userDataSource.getUserByUsername(username)
                if (user != null)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = user.toResponse()
                    )
                else
                    call.respond(HttpStatusCode.NotFound, "User not found")

            }
        }
    }
}