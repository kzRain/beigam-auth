package kz.beigam.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kz.beigam.data.source.UserDataSource

fun Route.users(userDataSource: UserDataSource) {
    route("user"){
        authenticate {
            get("companies") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", String::class)
                if (userId==null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val result = userDataSource.getAllCompanies(userId)
                call.respond(result)
            }

        }
    }
}