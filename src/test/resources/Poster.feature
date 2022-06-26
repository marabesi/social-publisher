Feature:  Post scheduled posts to twitter
  Background: With a new cli instance and the twitter credentials in place
    Given A new cli with date set to "2022-10-02T09:01:00Z"
    And the twitter credentials in place

  Scenario: Schedule a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    When I create a post with the text "Post to schedule"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show successfully message "Post has been scheduled"
    Then I clean the output
    And I set the post "1" to "twitter"
    Then I clean the output
    Then Poster should execute routine to send posts
    Then Show successfully message
    """
    Post 1 sent to twitter
    """
    Then I remove post "Post to schedule" from twitter

  Scenario: List scheduled posts in the future
    When I create a post with the text "Post to schedule-1"
    When I create a post with the text "Post to schedule-2"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-11-02T09:00:00Z"
    And I schedule the post with id "2" to be published at "2022-11-02T09:00:00Z"
    Then I clean the output
    And I set the post "1" to "twitter"
    Then I clean the output
    And I set the post "2" to "twitter"
    Then I clean the output
    Then Poster should show "Waiting for the date to come to publish post 1"
    Then Poster should show "Waiting for the date to come to publish post 2"

  Scenario: Avoid posting twice the same post
    When I create a post with the text "Post to schedule-3"
    When I create a post with the text "Post to schedule-4"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-09-02T09:00:00Z"
    And I schedule the post with id "2" to be published at "2022-09-03T08:00:00Z"
    And I set the post "1" to "twitter"
    Then I clean the output
    And I set the post "2" to "twitter"
    Then I clean the output
    Then Poster should execute routine to send posts
    Then Show successfully message
    """
    Post 1 sent to twitter
    Post 2 sent to twitter
    """
    Then I remove post "Post to schedule-3" from twitter
    Then I remove post "Post to schedule-4" from twitter
    Then I clean the output
    Then Poster should execute routine to send posts
    Then Show successfully message "There are no posts to be posted"
