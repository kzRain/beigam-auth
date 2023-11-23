package kz.beigam.data.source.user

import kz.beigam.data.models.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import kz.beigam.data.source.UserDataSource
import org.bson.types.ObjectId
import org.litote.kmongo.setValue

class MongoUserDataSource(
    db: CoroutineDatabase
): UserDataSource {

    private val users = db.getCollection<User>()
    override suspend fun getUserByUsername(username: String): User? =
        users.findOne(User::username eq username)

    override suspend fun insertUser(user: User): Boolean =
        users.insertOne(user).wasAcknowledged()

    override suspend fun getAllUsers(): List<User> =
        users.find().toList()

    override suspend fun getAllCompanies(userId: String): List<CompanyRoles> {
        val user = getUserById(userId)
        return user?.companies ?: emptyList()
    }

    override suspend fun getUserById(userId: String): User? =
        users.findOne(User::id eq ObjectId(userId))

    override suspend fun addCompany(userId: String, companyId: String, companyName: String, roles: List<UserRole>): Boolean {
        val user = getUserById(userId) ?: return false
        val existingUserRole = user.companies.firstOrNull() { it.companyId == companyId }
        val companyForUpdate =  if(existingUserRole==null) CompanyRoles(
            companyId, companyName, roles
        ) else {
            val existingRoles = existingUserRole.roles.toMutableList()
            roles.forEach {
                if (!existingRoles.contains(it)) {
                    existingRoles.add(it)
                }
            }
            CompanyRoles(
                companyId, companyName, existingRoles
            )
        }
        val userRoles = user.companies.filter { it.companyId != companyId }.plus(companyForUpdate)
        val resultUser =
            users.updateOne(User::id eq ObjectId(userId), setValue(User::companies, userRoles))
        println("resultUser = $resultUser")
        return true
    }
}