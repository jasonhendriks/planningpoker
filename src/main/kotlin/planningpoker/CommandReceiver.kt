package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.web.session.SseSessionManager
import kotlinx.coroutines.runBlocking

class CommandReceiver(
    val roomRepository: RoomRepository,
    val usersToRoom: AssignmentRepository
) {

    fun broadcastUpdate(me: User?) = runBlocking {
        me.let {
            usersToRoom.findAssignment(it)?.let { assignment ->
                SseSessionManager.broadcastUpdate(usersToRoom.findAssignments(assignment.room))
            }
        }
    }

}
