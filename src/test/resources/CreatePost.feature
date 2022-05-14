Feature: Create a post with a title
  Background: With a new cli instance and no configuration in place

  Scenario: Create post with text
    Given A new cli
    When I create a post with the text "Hello"
    Then Show successfully message "Post has been created"
    Then I clean the output

  Scenario: List the created post
    Given A new cli
    When I create a post with the text "Hello"
    Then I clean the output
    Then Show the created post "1. Hello"

  Scenario: Creates two posts and list them
    Given A new cli
    When I create a post with the text "caracters"
    When I create a post with the text "another"
    Then I clean the output
    Then Show the created post "1. caracters"
    Then Show the created post "2. another"
