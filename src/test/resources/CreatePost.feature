Feature: Create a post with a title
  Background: With a new cli instance and no configuration in place

  Scenario: Create post with text
    Given A new cli
    When I create a post with the text "Hello"
    Then Show successfully message "Post has been created"

  Scenario: List no posts found
    Given A new cli
    When I list the posts
    Then Show successfully message "No post found"

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

  Scenario: Text with more than 50 characters should show three dots
    Given A new cli
    When I create a post with the text "caracters, our online editor can help you to improve word choice and writing style, and, optionally, help you to detect grammar mistakes and plagiarism. To check word count, simply 1"
    Then I clean the output
    Then Show the created post "caracters, our online editor can help you to impro..."

  Scenario: Creates and lists posts
    Given A new cli
    When I list the posts
    Then Show successfully message "No post found"
    Then I clean the output
    When I create a post with the text "banana"
    Then Show successfully message "Post has been created"
    Then I clean the output
    When I create a post with the text "whatever"
    Then Show successfully message "Post has been created"
    Then I clean the output
    When I create a post with the text "random"
    Then Show successfully message "Post has been created"
    Then I clean the output
    When I list the posts
    Then Show the created post "1. banana"
    Then Show the created post "2. whatever"
    Then Show the created post "3. random"
