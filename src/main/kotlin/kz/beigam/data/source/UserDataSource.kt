package kz.beigam.data.source

import kz.beigam.data.models.CompanyRoles
import kz.beigam.data.models.User
import kz.beigam.data.models.UserRole

interface UserDataSource {

    suspend fun getUserByUsername(username: String): User?

    suspend fun insertUser(user: User): Boolean

    suspend fun getAllUsers(): List<User>

    suspend fun getAllCompanies(userId: String): List<CompanyRoles>

    suspend fun getUserById(userId: String): User?
    suspend fun addCompany(userId: String, companyId: String, companyName: String, roles: List<UserRole>): Boolean
}