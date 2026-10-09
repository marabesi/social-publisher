package application.socialnetwork

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object LinkedInOAuth {
    const val SCOPE = "openid profile w_member_social"
    const val AUTHORIZATION_BASE_URL = "https://www.linkedin.com"

    fun authorizationUrl(
        clientId: String,
        redirectUri: String,
        state: String,
        baseUrl: String = AUTHORIZATION_BASE_URL,
    ): String {
        val query =
            listOf(
                "response_type=code",
                "client_id=${encode(clientId)}",
                "redirect_uri=${encode(redirectUri)}",
                "scope=${encode(SCOPE)}",
                "state=${encode(state)}",
            ).joinToString("&")

        return "$baseUrl/oauth/v2/authorization?$query"
    }

    private fun encode(value: String): String =
        URLEncoder
            .encode(value, StandardCharsets.UTF_8)
            .replace("+", "%20")
}
