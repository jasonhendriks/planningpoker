package ca.hendriks.planningpoker.room

data class Room(val name: String) {

    private var voting: VotingState = VotingState.CLOSED

    fun openVoting() {
        voting = VotingState.OPEN
    }

    fun closeVoting() {
        voting = VotingState.CLOSED
    }

    fun isVotingOpen(): Boolean {
        return voting == VotingState.OPEN
    }
}

enum class VotingState {
    OPEN, CLOSED
}
