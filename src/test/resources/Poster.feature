Feature:  Post scheduled posts to twitter
  Background: With a new cli instance

  Scenario: Schedule a post created on "2022-10-02 at 09:00 PM" to be posted on "2022-10:02 at 10:00 PM"
    Given A new cli with date set to "2022-10-02T09:01:00Z"
    When I create a post with the text "Post to schedule"
    Then I clean the output
    And I schedule the post with id "1" to be published at "2022-10-02T09:00:00Z"
    Then Show successfully message "Post has been scheduled"
    Then I clean the output
    And I set the post "1" to "twitter"
    Then I clean the output
    Then Poster should send post "1" to twitter
