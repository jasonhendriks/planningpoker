package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.web.html.insertJoinRoomForm
import ca.hendriks.planningpoker.web.session.SseSessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.html.div
import kotlinx.html.stream.createHTML

class LeaveRoomCommand(val assignmentId: String, val user: User?, val receiver: CommandReceiver) : Command {

    private lateinit var content: String

    override fun execute() = runBlocking {
        val assignment = receiver.usersToRoom.findAssignment(assignmentId)
        if (assignment != null) {
            receiver.usersToRoom.unassign(assignmentId)
            val room = assignment.room
            SseSessionManager.broadcastUpdate(receiver.usersToRoom.findAssignments(room))
        }
        content = createHTML()
            .div { insertJoinRoomForm(user) }
    }

    fun getContent(): String = content

}
