package kz.beigam.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Company(
    @BsonId
    val id: ObjectId = ObjectId(),
    val companyName: String,
    val users: List<UserRoles> = listOf()
) {
    fun toDTO() = CompanyDTO(
        id = id.toString(),
        companyName = companyName
    )
}

@Serializable
class CompanyDTO(
    val id: String,
    val companyName: String)

@Serializable
data class UserRoles(
    val userId: String,
    val username: String,
    val roles: List<UserRole> = listOf()
)