package ca.hendriks.kotlinpoker.cucumber

import ca.hendriks.kotlinpoker.cucumber.Steps.TestDependices
import ca.hendriks.planningpoker.assignment.Assignment
import ca.hendriks.planningpoker.user.User
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking


class AssignmentSteps {

    private lateinit var assignment: Assignment

    @Given("user {string} joins room {string}")
    fun user_joins_room(userName: String, roomName: String) = runBlocking {
        val room = TestDependices.roomRepository.findOrCreateRoom(roomName)
        val user = User(userName)
        assignment = TestDependices.usersToRoom.assignUserToRoom(user, room)
    }

    @Given("voting is open")
    fun voting_is_open() {
        assignment.room.openVoting()
    }

    @Given("voting is closed")
    fun voting_is_closed() {
        assignment.room.closeVoting()
    }

    @When("the user attempts to vote")
    fun the_user_attempts_to_vote() {
        assignment = assignment.userVotes(true)
    }

    @Then("a vote should be recorded")
    fun a_vote_should_be_recorded() {
        assignment.vote shouldNotBe null
    }

    @Then("no vote should be recorded")
    fun no_vote_should_be_recorded() {
        assignment.vote shouldBe null
    }

}
