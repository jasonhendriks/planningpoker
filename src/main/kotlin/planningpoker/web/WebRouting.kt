package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.AssignmentRepository
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.routing.session.SseSessionManager
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.web.html.renderIndex
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

const val LOBBY_PATH = "/"

fun Application.configureRouting(
    usersToRoom: AssignmentRepository
) {

    val logger = LoggerFactory.getLogger("Routing")

    routing {
        staticResources("/css", "web")
        staticResources("/script", "web")

        route(LOBBY_PATH) {
            get {
                call.respondHtml {
                    logger.info { "Display homepage" }
                    val userSession: UserSession? = call.sessions.get()
                    val assignment = usersToRoom.findAssignment(user = userSession?.user)
                    renderIndex(user = userSession?.user, assignment = assignment)
                }
            }
        }

        route("/room/{room-name}") {
            get {
                logger.info { "Rendering room" }
                call.respondHtml {
                    renderIndex()
                }
            }
        }

        sse("/assignments/{id}/sse") {
            val assignmentId = call.parameters["id"]!!
            val assignment = usersToRoom.findAssignment(assignmentId)
            require(assignment != null) { "Assignment not found for id $assignmentId" }
            val room = assignment.room
            SseSessionManager.registerSession(this)
            SseSessionManager.broadcastUpdate(assignment, usersToRoom.findUsersForRoom(room))
            try {
                logger.info { "Client connected to SSE" }
                while (true) {
                    send(
                        ServerSentEvent(
                            "",
                            event = "keep-alive"
                        )
                    )
                    delay(1000)
                }
            } finally {
                logger.info { "Client disconnected from SSE" }
                SseSessionManager.removeSession(this)
                usersToRoom.unassign(assignmentId)
                SseSessionManager.broadcastUpdate(assignment, usersToRoom.findUsersForRoom(room))
            }
        }
    }

}
