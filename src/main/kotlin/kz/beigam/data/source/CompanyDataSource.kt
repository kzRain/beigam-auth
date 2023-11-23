package kz.beigam.data.source

import kz.beigam.data.models.Company
import kz.beigam.data.models.UserRole
import kz.beigam.data.models.UserRoles

interface CompanyDataSource {

    suspend fun getCompanyByName(companyName: String): Company?

    suspend fun insertCompany(company: Company): Boolean

    suspend fun getAllCompanies(): List<Company>

    suspend fun addUser(companyId: String, userId: String, roles: List<UserRole>): Boolean

    suspend fun updateUserRole(companyId: String, userId: String, roles: List<UserRole>): Boolean

    suspend fun getAllUsers(companyId: String): List<UserRoles>

    suspend fun getCompanyById(companyId: String): Company?
}