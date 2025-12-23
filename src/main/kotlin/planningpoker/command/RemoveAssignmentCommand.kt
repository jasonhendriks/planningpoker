package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.web.html.insertJoinRoomForm
import ca.hendriks.planningpoker.web.session.SseSessionManager
import kotlinx.coroutines.runBlocking
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.slf4j.LoggerFactory

class RemoveAssignmentCommand(val assignmentId: String, val user: User?, val receiver: CommandReceiver) : Command {

    private val logger = LoggerFactory.getLogger(RemoveAssignmentCommand::class.java)
    private var internalContent: String? = null
    val content: String
        get() = requireNotNull(internalContent) { "call execute() on the command before calling this getter" }

    override fun execute() = runBlocking {
        val assignment = receiver.usersToRoom.findAssignment(assignmentId)
        if (assignment != null) {
            receiver.usersToRoom.unassign(assignmentId)
            val room = assignment.room

            if (receiver.usersToRoom.findAssignments(assignment.room).isEmpty()) {
                receiver.roomRepository.deleteRoom(room)
                logger.info("Deleted room $room as it has no more users")
            }
            SseSessionManager.broadcastUpdate(receiver.usersToRoom.findAssignments(room))
        }
        internalContent = createHTML()
            .div { insertJoinRoomForm(user) }
    }

}
