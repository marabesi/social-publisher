Feature: Connect a LinkedIn account
  Background: With a new cli instance and no configuration in place
    Given A new cli

  Scenario: Generate the authorization URL and exchange the code for a token
    When I create a configuration with '{"storage":"csv","linkedin":{"clientId":"client-id","clientSecret":"client-secret","redirectUri":"https://example.com/callback"}}'
    Then I clean the output
    When I generate the linkedin authorization url
    Then the output should contain "https://www.linkedin.com/oauth/v2/authorization?"
    Then the output should contain "client_id=client-id"
    Then the output should contain "redirect_uri=https%3A%2F%2Fexample.com%2Fcallback"
    Then I clean the output
    When I connect linkedin using code "the-code"
    Then Show successfully message "LinkedIn account connected"
    Then I clean the output
    When I list the configuration
    Then the output should contain "test-linkedin-access-token"
    Then the output should contain "urn:li:person:test-member-id"
