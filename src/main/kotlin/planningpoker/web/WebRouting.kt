package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.LeaveRoomCommand
import ca.hendriks.planningpoker.command.SseCommand
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.html.renderIndex
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sse.sse
import org.slf4j.LoggerFactory

const val LOBBY_PATH = "/"

fun Application.configureRouting(receiver: CommandReceiver) {

    val logger = LoggerFactory.getLogger("WebRouting")
    val usersToRoom = receiver.usersToRoom
    val roomRepository = receiver.roomRepository

    routing {
        staticResources("/css", "web")
        staticResources("/script", "web")

        route(LOBBY_PATH) {
            get {
                logger.debug { "Display homepage" }
                val user = call.findUserInSession()
                LeaveRoomCommand(user, receiver).execute()
                call.respondText(
                    text = renderIndex(user),
                    contentType = ContentType.Text.Html
                )
            }
        }

        route("/room/{room-name}") {
            get {
                val roomName = call.findRoomNameOrRespondBadRequest()
                val user = call.findUserInSession()
                if (user != null) {
                    logger.debug { "Rendering room with session user" }
                    val room = roomRepository.findOrCreateRoom(roomName)
                    val assignment = usersToRoom.assignUserToRoom(user, room)
                    call.respondText(
                        text = renderIndex(user, assignment),
                        contentType = ContentType.Text.Html
                    )
                } else {
                    logger.debug { "Rendering room with no user" }
                    call.respondText(
                        text = renderIndex(),
                        contentType = ContentType.Text.Html
                    )
                }
            }
        }

        sse("/assignments/{id}/sse") {
            val assignmentId = call.findIdOrRespondBadRequest()
            SseCommand(this, assignmentId, receiver)
                .execute()
        }

    }

}
