package kz.beigam.service

import kz.beigam.data.models.User
import kz.beigam.data.models.requests.AuthRequest
import kz.beigam.data.source.UserDataSource
import kz.beigam.exceptions.UserException
import kz.beigam.security.hashing.HashingService

interface UserService {
    suspend fun createUser(user: AuthRequest): String
}

class UserServiceDefault(
    private val userDataSource: UserDataSource,
    private val hashingService: HashingService
): UserService {
    override suspend fun createUser(user: AuthRequest): String {
        val areFieldsBlank = user.username.isBlank() || user.password.isBlank()
        if (areFieldsBlank) {
            throw UserException("Fields not be blank")
        }

        val isPasswordShort = user.password.length < 8
        if (isPasswordShort) {
            throw UserException("Password length must not be less 8")
        }

        if (userDataSource.getUserByUsername(user.username) != null) {
            throw UserException("Username is already exists")
        }

        val saltedHash = hashingService.generateSaltedHash(user.password)
        val newUser = User(
            username = user.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.insertUser(newUser)
        if (!wasAcknowledged) {
            throw UserException("User was acknowledged")
        } else {
            return "OK"
        }
    }

}