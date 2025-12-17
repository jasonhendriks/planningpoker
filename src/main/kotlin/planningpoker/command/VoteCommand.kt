package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver

class VoteCommand(val value: String, val assignmentId: String, val receiver: CommandReceiver) : Command {
    override fun execute() {
        receiver.usersToRoom
            .vote(assignmentId, value)
            ?.let { receiver.broadcastUpdate(it.room) }
    }
}
