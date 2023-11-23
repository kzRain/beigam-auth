package kz.beigam.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kz.beigam.data.models.Company
import kz.beigam.data.models.UserRole
import kz.beigam.data.models.requests.CompanyRequest
import kz.beigam.data.source.CompanyDataSource

fun Route.companies(companyDataSource: CompanyDataSource) {
    route("company") {
        post("create") {
            val request = kotlin.runCatching { call.receiveNullable<CompanyRequest>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val existingCompany = companyDataSource.getCompanyByName(request.companyName)
            if (existingCompany != null) {
                call.respond(HttpStatusCode.NotAcceptable, "Company with name ${request.companyName} already exists")
                return@post
            }
            companyDataSource.insertCompany(Company(companyName = request.companyName))
            call.respond(HttpStatusCode.OK)
        }

        put("{id}/add_user/{user_id}") {
            val companyId = call.parameters["id"]
            val userId = call.parameters["user_id"]
            val roles = kotlin.runCatching { call.receiveNullable<List<UserRole>>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            if(companyDataSource.addUser(companyId!!, userId!!, roles)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotAcceptable)
            }
        }
//TODO Повторяюшийся код из за того что пока не нужен
        put("{id}/update_user_role/{user_id}") {
            val companyId = call.parameters["id"]
            val userId = call.parameters["user_id"]
            val roles = kotlin.runCatching { call.receiveNullable<List<UserRole>>() }.getOrNull() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            if(companyDataSource.addUser(companyId!!, userId!!, roles)) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotAcceptable)
            }
        }

        get("all") {
            val companies = companyDataSource.getAllCompanies().map { it.toDTO() }
            call.respond(companies)
        }

        get("{id}/users") {
            val companyId = call.parameters["id"]
            val users = companyDataSource.getAllUsers(companyId!!)
            call.respond(users)
        }
    }
}