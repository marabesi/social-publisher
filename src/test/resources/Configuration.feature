Feature: Configuration json that is required to use the cli
  Background: With a new cli instance and no configuration in place

  Scenario: Creates a configuration
    Given A new cli
    When I create a configuration with the path to "data"
    Then Show successfully message "Configuration has been stored"
