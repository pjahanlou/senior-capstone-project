Feature: Save journal in help request page

  Scenario: The countdown timer runs out of time
    Given a seizure has been detected
    When the countdown timer runs out of time
    Then then a journal gets saved to Firebase
      And the UI changes to show help has been notified
      And a text/email gets sent

  Scenario: The user is feeling like they are about to have a seizure
    Given the user is already in the help request page
    When the user clicks on the call now button
    Then then a journal gets saved to Firebase
    And the UI changes to show help has been notified
    And a text/email gets sent