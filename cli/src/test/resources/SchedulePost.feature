Feature: Schedule a post to be posted
  Background: With a new cli instance and no configuration in place
    Given A new cli with date set to "2022-10-02T09:01:00Z"

  Scenario: List empty list without posts scheduled
    Given A new cli
    When I list the scheduled posts
    Then Show successfully message "No posts scheduled"

  Scenario: Schedule a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to schedule"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show successfully message "Post has been scheduled using UTC timezone"
    Then I clean the output
    And I set the post "1" to "twitter"
    Then Show successfully message "Post 1 set to twitter"

  Scenario: List a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to schedule again"
    Then I clean the output
    Then I list the posts
    Then Show successfully message "1. Post to schedule again (22/280)"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show the scheduled post "1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)"

  Scenario: Removes scheduled post
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to be removed"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then I clean the output
    Then I remove schedule from post with id "1"
    Then Show successfully message "Schedule 1 has been removed from post 1"

  Scenario: Schedule a post with a local date time converted to UTC using the configured timezone
    Given A new cli
    When I create a configuration with '{"storage":"csv","timezone":"Europe/Madrid","fileName":"tz"}'
    When I create a post with the text "Post to schedule in Madrid"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00"
    Then Show successfully message "Post has been scheduled using Europe/Madrid timezone"
    Then I clean the output
    And I list the scheduled posts
    Then Show successfully message "1. Post with id 1 will be published on 2022-10-02T07:00:00Z (Twitter)"

  Scenario: Pick a random post for a given day
    When I create a configuration of type csv and store files under the name "random-e2e"
    When I create a post with the text "Random post"
    Then I clean the output
    When I pick a random post to be scheduled on "2022-10-10"
    Then the output should contain "Post 1 has been randomly scheduled for"
    And the output should contain "using UTC timezone"
    And I list the scheduled posts
    Then the output should contain "Post with id 1 will be published on 2022-10-10"