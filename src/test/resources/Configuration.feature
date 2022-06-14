Feature: Configuration json that is required to use the cli
  Background: With a new cli instance and no configuration in place

  Scenario: Creates a configuration
    Given A new cli
    When I create a configuration with the name to "e2e-file"
    Then Show successfully message "Configuration has been stored"
    Then I clean the output
    Then I list the configuration from "e2e-file"
    Then I see the configuration '{"path":"data/e2e-file.json"}'
