Feature: Configuration json that is required to use the cli
  Background: With a new cli instance

  Scenario: Creates a configuration
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    Then Show successfully message "Configuration has been stored"
    Then I clean the output
    Then I list the configuration
    Then I see the configuration '{"fileName":"e2e-file","storage":"csv","timezone":"UTC"}'

  Scenario: Creates a configuration with twitter credentials
    Given A new cli
    When I create a configuration with '{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"}}'
    Then Show successfully message "Configuration has been stored"
    Then I clean the output
    Then I list the configuration
    Then I see the configuration '{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"},"timezone":"UTC"}'
