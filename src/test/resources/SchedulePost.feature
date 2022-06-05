Feature: Schedule a post to be posted
  Background: With a new cli instance and no configuration in place

  Scenario: Schedule a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    Given A new cli
    When I create a post with the text "Post to schedule"
    Then I clean the output
    And I and schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show successfully message "Post has been scheduled"

  Scenario: List a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    Given A new cli
    When I create a post with the text "Post to schedule again"
    Then I clean the output
    And I and schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show the scheduled post "1. Post with id 1 will be published on 2022-10-02T09:00:00Z"
