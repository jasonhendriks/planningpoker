package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.room.Room
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.web.session.SseSessionManager
import kotlinx.coroutines.runBlocking

class CommandReceiver(
    val roomRepository: RoomRepository,
    val usersToRoom: AssignmentRepository
) {

    fun broadcastUpdate(room: Room) = runBlocking {
        val assignments = usersToRoom.findAssignments(room)
        SseSessionManager.broadcastUpdate(assignments)
    }

}
