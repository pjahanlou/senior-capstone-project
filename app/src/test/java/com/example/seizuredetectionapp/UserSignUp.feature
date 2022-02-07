Feature: User sign up

  Scenario: The user is signing up for the application
    Given the user is in the sign up page
    When they fill out their information
      And hit the sign up button
    Then a verification email gets sent to them