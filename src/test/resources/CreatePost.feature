Feature: Create a post with a title
  Scenario: Create post with title
    When I create a post with the title "Hello"
    Then Show successfull menssage "Post has been created"