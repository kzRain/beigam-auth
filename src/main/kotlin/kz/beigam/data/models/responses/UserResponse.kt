package kz.beigam.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val username: String,
    val password: String,
    val salt: String
)