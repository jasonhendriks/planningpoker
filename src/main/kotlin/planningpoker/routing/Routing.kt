package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.AssignmentRepository
import ca.hendriks.planningpoker.debug
import ca.hendriks.planningpoker.html.insertJoinRoomForm
import ca.hendriks.planningpoker.html.insertSseFragment
import ca.hendriks.planningpoker.html.renderIndex
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.user.User
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.getOrSet
import io.ktor.server.sessions.sessions
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.slf4j.LoggerFactory
import kotlin.uuid.ExperimentalUuidApi

const val LOBBY_PATH = "/"

fun Application.configureRouting() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomRepository = RoomRepository()
    val usersToRoom = AssignmentRepository()
    roomRepository.createRoom("Charlie")

    routing {
        staticResources("/css", "web")
        route(LOBBY_PATH) {
            header("HX-Request", "true") {
                get(LOBBY_PATH) {
                    logger.info { "HTMX -> Back to Lobby" }
                    val userSession: UserSession? = call.sessions.get()
                    val user = userSession?.user
                    if (user != null) {
                        val room = usersToRoom.findAssignment(user)?.room!!
                        usersToRoom.unassignUser(user)
                        SseSessionManager.broadcastUpdate(room, usersToRoom.findUsersForRoom(room))
                    }
                    call.respondText(createHTML().div { insertJoinRoomForm(user) }, contentType = ContentType.Text.Html)
                }
            }
            get(LOBBY_PATH) {
                call.respondHtml {
                    logger.info { "Display homepage" }
                    val userSession: UserSession? = call.sessions.get()
                    val assignment = usersToRoom.findAssignment(user = userSession?.user)
                    renderIndex(user = userSession?.user, assignment = assignment)
                }
            }
        }

        route("/assignments/{room-name}") {
            header("HX-Request", "true") {

                post {
                    val roomName = call.parameters["room-name"]
                    val userName = call.parameters["user-name"]

                    if (roomName == null || roomName.trim().isEmpty()) {
                        call.respond(BadRequest, "room-name is required")
                        return@post
                    }
                    val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)

                    val user = findUserOrCreateUser(call, userName)
                    logger.debug { "Found/Created $user in the session" }
                    val assignment = usersToRoom.assignUserToRoom(user, room)

                    call.response.headers.append("HX-Replace-Url", "/rooms/$roomName")
                    call.respondText(
                        createHTML().div { insertSseFragment(assignment) },
                        contentType = ContentType.Text.Html
                    )
                }

            }
        }

        route("/rooms/{room-name}") {
            get {
                logger.info { "Rendering room" }
                call.respondHtml {
                    val roomName = call.parameters["room-name"]
                    val charlie = roomRepository.findRoom(roomName!!)!!
                    renderIndex()
                }
            }
        }

        sse("/sse/assignments/{id}") {
            val assignmentId = call.parameters["id"]!!
            val assignment = usersToRoom.findAssignment(assignmentId)
            require(assignment != null) { "Assignment not found for id $assignmentId" }
            val room = assignment.room
            SseSessionManager.registerSession(this)
            SseSessionManager.broadcastUpdate(room, usersToRoom.findUsersForRoom(room))
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
                SseSessionManager.broadcastUpdate(room, usersToRoom.findUsersForRoom(room))
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
