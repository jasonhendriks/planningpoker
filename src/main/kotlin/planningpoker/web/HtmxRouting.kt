package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.AssignmentRepository
import ca.hendriks.planningpoker.debug
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.routing.session.SseSessionManager
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.web.html.insertJoinRoomForm
import ca.hendriks.planningpoker.web.html.insertSseFragment
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.getOrSet
import io.ktor.server.sessions.sessions
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.slf4j.LoggerFactory
import kotlin.uuid.ExperimentalUuidApi

fun Application.configureHtmxRouting(
    roomRepository: RoomRepository,
    usersToRoom: AssignmentRepository
) {

    val logger = LoggerFactory.getLogger("Routing")

    routing {
        header("HX-Request", "true") {

            route(LOBBY_PATH) {
                delete("/assignments/{id}") {
                    logger.info { "HTMX -> Back to Lobby" }
                    val assignmentId = call.parameters["id"]!!
                    val assignment = usersToRoom.findAssignment(assignmentId)
                    if (assignment != null) {
                        usersToRoom.unassign(assignmentId)
                        val room = assignment.room
                        SseSessionManager.broadcastUpdate(assignment, usersToRoom.findUsersForRoom(room))
                    }
                    val userSession: UserSession? = call.sessions.get()
                    call.response.headers.append("HX-Replace-Url", "/")
                    call.respondText(
                        createHTML().div { insertJoinRoomForm(userSession?.user) },
                        contentType = ContentType.Text.Html
                    )
                }
            }

            route("/assignments") {
                post {
                    val roomName = call.parameters["room-name"]
                    val userName = call.parameters["user-name"]

                    if (roomName == null || roomName.trim().isEmpty()) {
                        call.respond(BadRequest, "A Room Name is required")
                        return@post
                    }
                    val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)

                    val user = findUserOrCreateUser(call, userName)
                    logger.debug { "Found/Created $user in the session" }
                    val assignment = usersToRoom.assignUserToRoom(user, room)

                    call.response.headers.append("HX-Replace-Url", "/room/$roomName")
                    call.respondText(
                        createHTML().div { insertSseFragment(assignment) },
                        contentType = ContentType.Text.Html
                    )
                }

            }

        }

    }

}

@OptIn(ExperimentalUuidApi::class)
private fun findUserOrCreateUser(call: RoutingCall, userName: String?): User {
    if (userName != null && !userName.trim().isEmpty()) {
        val userSession = call.sessions.getOrSet<UserSession> { UserSession(User()) }
        userSession.user.name = userName
        return userSession.user
    } else {
        val userSession: UserSession? = call.sessions.get()
        require(userSession?.user != null) { "User not found in session" }
        return userSession.user
    }
}
