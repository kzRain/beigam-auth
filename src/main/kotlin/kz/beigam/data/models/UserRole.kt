package kz.beigam.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(accessPoint: Int) {
        USER(0), ADMIN(100)
}