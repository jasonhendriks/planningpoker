package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.html.insertRoomFragment
import ca.hendriks.planningpoker.html.insertSseFragment
import ca.hendriks.planningpoker.html.renderIndex
import ca.hendriks.planningpoker.html.renderJoinRoomForm
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import io.ktor.util.logging.debug
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

fun Application.configureRouting() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomRepository = RoomRepository()
    roomRepository.createRoom("Charlie")

    routing {
        get("/") {
            call.respondHtml {
                renderIndex()
            }
        }

        get("/rooms") {
            logger.info { "List rooms" }
            call.respondHtml {
                renderJoinRoomForm()
            }
        }

        route("/rooms/{room-name}") {
            header("HX-Request", "true") {
                get {
                    logger.info { "Rendering room fragments" }
                    val roomName = call.parameters["room-name"]
                    val charlie = roomRepository.findRoom(roomName!!)!!
                    call.respondText(insertRoomFragment(charlie), contentType = ContentType.Text.Html)
                }
                post {
                    val roomName = call.parameters["room-name"]

                    if (roomName == null) {
                        call.respond(BadRequest, "room-name is required")
                        return@post
                    }

                    val session = call.sessions.get<UserSession>()
                    if (session != null) {
                        logger.debug { "Found user ${session.userName} in session" }
                    }

                    val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)
                    call.respondText(insertSseFragment(room), contentType = ContentType.Text.Html)
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

        post("/rooms/{room-name}/users/{user-name}") {
            val roomName = call.parameters["room-name"]
            val userName = call.parameters["user-name"]

            if (roomName == null || roomName.trim().isEmpty()) {
                call.respond(BadRequest, "room-name is required")
                return@post
            }

            if (userName == null || userName.trim().isEmpty()) {
                call.respond(BadRequest, "user-name is required")
                return@post
            }

            val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)
            val user = room.addUser(userName)
            if (user == null) {
                logger.debug("User $userName is already in room $roomName")
                call.respond(Conflict, "User $userName already exists in room $roomName")
                return@post
            }

            call.sessions.set(UserSession(userName))
            logger.debug("Set $userName into the session")

            call.respondText(insertSseFragment(room), contentType = ContentType.Text.Html)
        }

        sse("/sse/sse-{room-name}") {
            val roomName = call.parameters["room-name"]
            val room = roomRepository.findRoom(roomName!!)!!
            SseSessionManager.registerSession(this)
            var eventName = "update"
            try {
                logger.info() { "Client connected to SSE" }
                while (true) {
                    send(ServerSentEvent(data = insertRoomFragment(room), event = eventName))
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
