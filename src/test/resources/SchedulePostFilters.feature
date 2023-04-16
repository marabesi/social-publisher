Feature: Schedule a post to be posted
  Background: With a new cli instance and no configuration in place
    Given A new cli with date set to "2022-10-02T09:01:00Z"

  Scenario: Show no posts found if none matches the criteria
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post with future date 1"
    Then I clean the output
    Then I list the posts
    Then Show successfully message "1. Post with future date 1"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-01-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-02-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-03-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts with the end date for "2020-01-02T09:00:00Z"
    Then Show successfully message "No posts scheduled"

  Scenario: Show no posts found if none matches the criteria
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post with future date 1"
    Then I clean the output
    Then I list the posts
    Then Show successfully message "1. Post with future date 1"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts starting from "2023-10-01T09:00:00Z"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2023-10-02T09:00:00Z
    """
