Feature: Schedule a post to be posted
  Background: With a new cli instance and no configuration in place
    Given A new cli with date set to "2022-10-02T09:01:00Z"

  Scenario: Show no posts found if none matches the criteria
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post with future date 1"
    Then I clean the output
    Then I list the posts
    Then Show successfully message "1. Post with future date 1 (23/280)"
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
    Then Show successfully message "1. Post with future date 1 (23/280)"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts starting from "2023-10-01T09:00:00Z"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2023-10-02T09:00:00Z (Twitter)
    """

  Scenario: Filter scheduled posts by any property
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "release notes"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts filtering by "post.text=release notes"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2023-10-02T09:00:00Z (Twitter)
    """

  Scenario: Filter scheduled posts by date parts
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to filter by date"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts filtering by "day=02&month=10&year=2022"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
    """

  Scenario: Filter scheduled posts showing no results when nothing matches
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to filter"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts filtering by "post.text=missing"
    Then Show successfully message "No posts scheduled"

  Scenario: Order scheduled posts by publish date
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to order"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts ordering by "publish_date=asc"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2021-10-02T09:00:00Z (Twitter)
    2. Post with id 1 will be published on 2023-10-02T09:00:00Z (Twitter)
    """

  Scenario: Filter and order scheduled posts together
    Given A new cli
    When I create a configuration of type csv and store files under the name "e2e-file"
    When I create a post with the text "Post to filter and order"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2023-10-02T09:00:00Z"
    And I schedule the post with id "1" to be published at "2021-01-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts filtering by "month=10" ordering by "publish_date=desc"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2023-10-02T09:00:00Z (Twitter)
    2. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
    """

  Scenario: Interpret date filters using the configured timezone
    Given A new cli
    When I create a configuration with '{"storage":"csv","timezone":"Europe/Madrid","fileName":"tz-filter"}'
    When I create a post with the text "Post to filter in Madrid"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then I clean the output
    When I list the scheduled posts starting from "2022-10-02T08:30:00"
    Then Show successfully message
    """
    1. Post with id 1 will be published on 2022-10-02T09:00:00Z (Twitter)
    """
