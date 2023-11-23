package kz.beigam.data.models

import kotlinx.serialization.Serializable
import kz.beigam.data.models.responses.UserResponse
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val username: String,
    val password: String,
    val salt: String,
    val companies: List<CompanyRoles> = listOf()
) {
    fun toResponse() = UserResponse(
        username = username,
        password = password,
        salt = salt
    )
}

@Serializable
data class CompanyRoles(
    val companyId: String,
    val companyName: String,
    val roles: List<UserRole> = listOf()
)

