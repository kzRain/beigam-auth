package kz.beigam.data

import kz.beigam.data.models.User

interface UserDataSource {

    suspend fun getUserByUsername(username: String): User?

    suspend fun insertUser(user: User): Boolean

    suspend fun getAllUsers(): List<User>
}