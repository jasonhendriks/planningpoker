package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.Receiver
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.web.html.insertJoinRoomForm
import ca.hendriks.planningpoker.web.session.SseSessionManager
import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.runBlocking
import kotlinx.html.div
import kotlinx.html.stream.createHTML

class LeaveRoomCommand(val assignmentId: String, val receiver: Receiver) : Command {
    override fun execute() = runBlocking {
        val assignment = receiver.usersToRoom.findAssignment(assignmentId)
        if (assignment != null) {
            receiver.usersToRoom.unassign(assignmentId)
            val room = assignment.room
            SseSessionManager.broadcastUpdate(receiver.usersToRoom.findAssignments(room))
        }
        val userSession: UserSession? = receiver.call.sessions.get()
        receiver.call.response.headers.append("HX-Replace-Url", "/")
        receiver.call.respondText(
            createHTML().div { insertJoinRoomForm(userSession?.user) },
            contentType = ContentType.Text.Html
        )
    }
}
