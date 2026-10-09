package adapters.inbound.rest.dto

data class HomeResponse(
    val name: String,
    val resources: Map<String, String>,
)
