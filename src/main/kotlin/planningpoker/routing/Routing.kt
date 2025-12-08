package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.UserToRoomRepository
import ca.hendriks.planningpoker.html.insertJoinRoomForm
import ca.hendriks.planningpoker.html.insertRoomFragment
import ca.hendriks.planningpoker.html.insertSseFragment
import ca.hendriks.planningpoker.html.renderIndex
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.user.User
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.header
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

val LOBBY_PATH = "/"

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomRepository = RoomRepository()
    val usersToRoom = UserToRoomRepository()
    roomRepository.createRoom("Charlie")

    routing {
        route(LOBBY_PATH) {
            header("HX-Request", "true") {
                get(LOBBY_PATH) {
                    logger.info { "HTMX -> Back to Lobby" }
                    val userSession: UserSession? = call.sessions.get()
                    val user = userSession?.user
                    if (user != null) {
                        usersToRoom.unassignUser(user)
                    }
                    call.respondText(createHTML().div { insertJoinRoomForm(user) }, contentType = ContentType.Text.Html)
                }
            }
            get(LOBBY_PATH) {
                call.respondHtml {
                    logger.info { "Display homepage" }
                    renderIndex()
                }
            }
        }

        route("/rooms/{room-name}") {
            header("HX-Request", "true") {

                get {
                    val roomName = call.parameters["room-name"]
                    val userName = call.parameters["user-name"]

                    if (roomName == null || roomName.trim().isEmpty()) {
                        call.respond(BadRequest, "room-name is required")
                        return@get
                    }
                    val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)

                    if (userName != null && !userName.trim().isEmpty()) {
                        val userSession = call.sessions.getOrSet<UserSession> { UserSession(User()) }
                        usersToRoom.assignUserToRoom(userSession.user, room)
                        userSession.user.name = userName
                        logger.debug("Set $userName into the session")
                    } else {
                        val userSession: UserSession? = call.sessions.get()
                        if (userSession != null) {
                            usersToRoom.assignUserToRoom(userSession.user, room)
                        }
                    }

                    call.response.headers.append("HX-Replace-Url", "/rooms/$roomName")
                    call.respondText(createHTML().div { insertSseFragment(room) }, contentType = ContentType.Text.Html)
                }

            }
        }

        route("/rooms/{room-name}") {
            get {
                logger.info { "Rendering room" }
                call.respondHtml {
                    val roomName = call.parameters["room-name"]
                    val charlie = roomRepository.findRoom(roomName!!)!!
                    renderIndex(charlie)
                }
            }
        }

        sse("/sse/sse-{room-name}") {
            val roomName = call.parameters["room-name"]
            val room = roomRepository.findRoom(roomName!!)!!
            SseSessionManager.registerSession(this)
            var eventName = "update"
            try {
                logger.info() { "Client connected to SSE" }
                while (true) {
                    send(
                        ServerSentEvent(
                            data = insertRoomFragment(room, usersToRoom.findUsersForRoom(room)),
                            event = eventName
                        )
                    )
                    eventName = "keep-alive"
                    delay(1000)
                }
            } finally {
                logger.info() { "Client disconnected from SSE" }
                SseSessionManager.removeSession(this)
            }
        }

    }
}
