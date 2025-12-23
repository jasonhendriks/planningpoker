package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User

class LeaveRoomCommand(val user: User?, val receiver: CommandReceiver) : Command {
    override fun execute() {
        val assignment = receiver.usersToRoom.findAssignment(user)
        if (assignment != null) {
            RemoveAssignmentCommand(assignment.id, user, receiver)
                .execute()
        }
    }
}
