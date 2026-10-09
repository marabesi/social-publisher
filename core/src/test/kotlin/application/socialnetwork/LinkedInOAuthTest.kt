package application.socialnetwork

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LinkedInOAuthTest {
    @Test
    fun `builds the authorization url with encoded parameters`() {
        val url =
            LinkedInOAuth.authorizationUrl(
                clientId = "my client",
                redirectUri = "https://example.com/callback",
                state = "abc",
            )

        assertTrue(url.startsWith("https://www.linkedin.com/oauth/v2/authorization?"))
        assertTrue(url.contains("response_type=code"))
        assertTrue(url.contains("client_id=my%20client"))
        assertTrue(url.contains("redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"))
        assertTrue(url.contains("scope=openid%20profile%20w_member_social"))
        assertTrue(url.contains("state=abc"))
    }
}
