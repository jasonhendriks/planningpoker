Feature: Voting

  Scenario: A user votes when voting is closed

    Given user "Jason" joins room "Charlie"
    Given voting is closed
    When the user attempts to vote
    Then no vote should be recorded

  Scenario: A user votes when voting is open

    Given user "Jason" joins room "Charlie"
    Given voting is open
    When the user attempts to vote
    Then a vote should be recorded
