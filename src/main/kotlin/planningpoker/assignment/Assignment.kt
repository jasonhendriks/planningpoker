package ca.hendriks.planningpoker.assignment

import ca.hendriks.planningpoker.room.Room
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.uuidSupplier

data class Assignment(
    val user: User,
    val room: Room,
    val id: String = uuidSupplier().invoke()
) {

    private var vote: Boolean? = null

    fun userVotes() {
        if (room.isVotingOpen())
            vote = true
    }

    fun getVote(): Boolean? {
        return vote
    }
}
