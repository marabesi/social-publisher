Feature: Configuration json that is required to use the cli
  Background: With a new cli instance

  Scenario: Creates a configuration
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    Then Show successfully message "Configuration has been stored"
    Then I clean the output
    Then I list the configuration
    Then I see the configuration '{"storage":"csv","fileName":"e2e-file.json"}'
