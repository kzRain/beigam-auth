package kz.beigam.security.token

interface TokenService {

    fun generate(
        config: TokenConfig,
        vararg claim: TokenClaim
    ): String
}