package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.html.renderIndex
import ca.hendriks.planningpoker.html.renderRoom
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.room.RoomService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.routing.accept
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

fun Application.configureRouting() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomService = RoomService()
    val roomRepository = RoomRepository()
    roomRepository.createRoom("Charlie")

    routing {
        get("/") {
            call.respondHtml {
                renderIndex(roomRepository)
            }
        }

        route("/rooms/{room-name}") {
            accept(ContentType.Text.Html) {
                get {
                    logger.info() { "Rendering room" }
                    call.respondHtml {
                        val roomName = call.parameters["room-name"]
                        val charlie = roomRepository.findRoom(roomName!!)!!
                        renderRoom(charlie)

                    }
                }
            }
        }

        sse("/sse/{room-name}") {
            heartbeat {
                period = 10.milliseconds
                event = ServerSentEvent("heartbeat")
            }
            try {
                logger.info() { "Client subscribed to SSE" }
                repeat(100) {
                    send(ServerSentEvent("this is SSE #$it"))
                    delay(1000)
                }
            } catch (exception: RuntimeException) {
                logger.info { exception }
            }
        }

        post("/rooms/{room-name}/users/{user-name}") {
            val roomName = call.parameters["room-name"]
            val userName = call.parameters["user-name"]

            if (roomName == null || userName == null) {
                call.respond(BadRequest, "room-name and user-name are required")
                return@post
            }

            val room = roomRepository.findRoom(roomName) ?: roomRepository.createRoom(roomName)
            val user = room.addUser(userName)
            if (user == null) {
                call.respond(Conflict, "User $userName already exists in room $roomName")
                return@post
            }
            call.respond(OK, "<h1>Welcome $user to room $roomName</h1>")
        }

    }
}
