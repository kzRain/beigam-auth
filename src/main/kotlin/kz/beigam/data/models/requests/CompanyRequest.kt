package kz.beigam.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class CompanyRequest(
    val companyName: String
)