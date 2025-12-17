package ca.hendriks.planningpoker.room

data class Room(val name: String) {

    private var voting: VotingState = VotingState.NEW

    fun openVoting() {
        voting = VotingState.OPEN
    }

    fun closeVoting() {
        voting = VotingState.CLOSED
    }

    fun isVotingOpen(): Boolean {
        return voting.isOpen()
    }

    fun isVotingNew(): Boolean {
        return voting.isNew()
    }

}

enum class VotingState {
    NEW, OPEN, CLOSED;

    fun isNew() = this == NEW
    fun isOpen() = this == OPEN
}
