package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.routing.session.SseSessionManager
import ca.hendriks.planningpoker.user.User
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.coroutines.runBlocking

class Receiver(
    val call: ApplicationCall,
    val roomRepository: RoomRepository,
    val usersToRoom: AssignmentRepository,
    val me: User?
) {

    fun broadcastUpdate() = runBlocking {
        me.let {
            usersToRoom.findAssignment(it)?.let { assignment ->
                SseSessionManager.broadcastUpdate(assignment, usersToRoom.findAssignments(assignment.room))
            }
        }
    }

    fun respondWithNoContent() = runBlocking {
        call.respond(HttpStatusCode.NoContent, "")
    }


}
