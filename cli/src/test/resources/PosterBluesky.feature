Feature:  Post scheduled posts to bluesky
  Background: With a new cli instance and the bluesky credentials in place
    Given A new cli with date set to "2022-10-02T09:01:00Z"
    And the bluesky credentials in place

  @interactsWithBluesky
  Scenario: Schedule a post created on "2022-10-02 at 09:00 PM" to be posted on bluesky
    When I create a post with the text "Post to bluesky"
    Then I clean the output
    And I schedule the post with id "1" for "BLUESKY" to be published at "2022-10-02T09:00:00Z"
    Then Show successfully message "Post has been scheduled using UTC timezone"
    Then I clean the output
    And I set the post "1" to "bluesky"
    Then I clean the output
    Then Poster should execute routine to send posts
    Then Show successfully message
    """
    Post 1 sent to bluesky
    """