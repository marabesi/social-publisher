Feature: Configuration json that is required to use the cli
  Background: With a new cli instance and no configuration in place

  Scenario: Creates a configuration
    Given A new cli
    When I create a configuration with the path to "e2e"
    Then Show successfully message "Configuration has been stored"
    Then I clean the output
    Then I list the configuration from "e2e"
    Then I see the configuration '{"path":"e2e/configuration.json"}'
