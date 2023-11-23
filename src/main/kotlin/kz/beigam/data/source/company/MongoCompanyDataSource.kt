package kz.beigam.data.source.company

import kz.beigam.data.models.Company
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import kz.beigam.data.models.User
import kz.beigam.data.models.UserRole
import kz.beigam.data.models.UserRoles
import kz.beigam.data.source.CompanyDataSource
import kz.beigam.data.source.user.MongoUserDataSource
import org.bson.types.ObjectId
import org.litote.kmongo.setValue

class MongoCompanyDataSource(
    db: CoroutineDatabase
): CompanyDataSource {

    private val companies = db.getCollection<Company>()
    private val userSource = MongoUserDataSource(db)
    override suspend fun getCompanyByName(companyName: String): Company? =
        companies.findOne(Company::companyName eq companyName)

    override suspend fun insertCompany(company: Company): Boolean =
        companies.insertOne(company).wasAcknowledged()

    override suspend fun getAllCompanies(): List<Company> =
        companies.find().toList()

    override suspend fun addUser(companyId: String, userId: String, roles: List<UserRole>): Boolean {
        val user = userSource.getUserById(userId)
        val company = getCompanyById(companyId)
        if (user == null|| company==null) return false
        val existingUserRole = company.users.firstOrNull() { it.userId == userId }
        val userForUpdate =  if(existingUserRole==null) UserRoles(
            userId, user.username, roles
        ) else {
            val existingRoles = existingUserRole.roles.toMutableList()
            roles.forEach {
                if (!existingRoles.contains(it)) {
                    existingRoles.add(it)
                }
            }
            UserRoles(
                userId, user.username, existingRoles
            )
        }
        val userRoles = company.users.filter { it.userId != userId }.plus(userForUpdate)
        val resultCompany =
            companies.updateOne(Company::id eq ObjectId(companyId), setValue(Company::users, userRoles))
        userSource.addCompany(userId, companyId, company.companyName, roles)
        println("resultCompany = $resultCompany")
        return true
    }

    override suspend fun updateUserRole(companyId: String, userId: String, roles: List<UserRole>): Boolean =addUser(companyId, userId, roles)

    override suspend fun getAllUsers(companyId: String): List<UserRoles> {
        val company = getCompanyById(companyId)
        return if (company==null) emptyList()
        else company.users
    }

    override suspend fun getCompanyById(companyId: String): Company? =
        companies.findOne(Company::id eq ObjectId(companyId))
}