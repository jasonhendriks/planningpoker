package ca.hendriks.planningpoker.assignment

import ca.hendriks.planningpoker.room.Room
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.uuidSupplier

data class Assignment(
    val user: User,
    val room: Room,
    val id: String = uuidSupplier().invoke(),
    val vote: Boolean? = null
) {
    fun userVotes(vote: Boolean): Assignment {
        return if (room.isVotingOpen())
            this.copy(vote = vote)
        else
            this
    }
}
