package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.html.renderIndex
import ca.hendriks.planningpoker.html.renderJoinRoomForm
import ca.hendriks.planningpoker.html.renderRoom
import ca.hendriks.planningpoker.html.renderSse
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.routing.accept
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.util.logging.debug
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
            accept(ContentType.Text.Html) {
                get {
                    logger.info { "Rendering room" }
                    call.respondHtml {
                        val roomName = call.parameters["room-name"]
                        val charlie = roomRepository.findRoom(roomName!!)!!
                        renderRoom(charlie)
                    }
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
                    call.respondHtml {
                        renderSse(room)
                    }
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
            val user = room.addUser(userName!!)
            if (user == null) {
                logger.debug("User $userName is already in room $roomName")
                call.respond(Conflict, "User $userName already exists in room $roomName")
                return@post
            }

            call.sessions.set(UserSession(userName))
            logger.debug("Set $userName into the session")

            call.respondHtml {
                renderSse(room)
            }
        }

    }
}
