package kz.beigam

import io.ktor.server.application.*
import kz.beigam.data.MongoUserDataSource
import kz.beigam.plugins.*
import kz.beigam.security.hashing.SHA256HashingService
import kz.beigam.security.token.JwtTokenService
import kz.beigam.security.token.TokenConfig
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val mongoUser = environment.config.property("mongo.username").getString()//System.getenv("MONGO_PASSWORD")
    val mongoPassword = environment.config.property("mongo.password").getString()//System.getenv("MONGO_PASSWORD")
    val dbName = environment.config.property("mongo.database").getString()
    val host = environment.config.property("mongo.host").getString()

    val db = KMongo.createClient(
        connectionString = "mongodb://$mongoUser:$mongoPassword@$host/$dbName?retryWrites=true&w=majority"
    )
        .coroutine
        .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        exiresIn = 365L * 1000L * 60L * 24L,
        secret = "secret"//System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )

}
