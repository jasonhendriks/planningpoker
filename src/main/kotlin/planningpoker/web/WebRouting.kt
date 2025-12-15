package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.CloseVotingCommand
import ca.hendriks.planningpoker.command.OpenVotingCommand
import ca.hendriks.planningpoker.command.SseCommand
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.html.renderIndex
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sse.sse
import kotlinx.html.html
import kotlinx.html.stream.createHTML
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
                val userSession: UserSession? = call.sessions.get()
                val assignment = usersToRoom.findAssignment(user = userSession?.user)
                if (assignment != null) {
                    usersToRoom.unassign(assignment.id)
                }
                call.respondText(
                    createHTML().html { renderIndex(user = userSession?.user, assignment = assignment) },
                    contentType = ContentType.Text.Html
                )
            }
        }

        route("/room/{room-name}") {
            get {
                val roomName = call.parameters["room-name"]
                if (roomName == null || roomName.trim().isEmpty()) {
                    call.respond(BadRequest, "A Room Name is required")
                    return@get
                }
                val userSession: UserSession? = call.sessions.get()
                if (userSession?.user != null) {
                    logger.debug { "Rendering room with session user" }
                    val room = roomRepository.findOrCreateRoom(roomName)
                    val assignment = usersToRoom.assignUserToRoom(userSession.user, room)
                    call.respondHtml {
                        renderIndex(userSession.user, assignment)
                    }
                } else {
                    logger.debug { "Rendering room with no user" }
                    call.respondHtml {
                        renderIndex()
                    }
                }
            }
        }

        sse("/assignments/{id}/sse") {
            val assignmentId = call.parameters["id"]!!
            SseCommand(this, assignmentId, receiver)
                .execute()
        }

        route("/room/{room-name}/voting") {
            post {
                call.parameters["room-name"]
                    ?.let {
                        val userSession: UserSession? = call.sessions.get()
                        OpenVotingCommand(
                            roomName = it,
                            me = userSession?.user,
                            receiver = receiver
                        )
                            .execute()
                        call.respond(HttpStatusCode.NoContent, "")
                    }
            }
            delete {
                call.parameters["room-name"]
                    ?.let {
                        val userSession: UserSession? = call.sessions.get()
                        CloseVotingCommand(
                            roomName = it,
                            me = userSession?.user,
                            receiver = receiver
                        )
                            .execute()
                        call.respond(HttpStatusCode.NoContent, "")
                }
            }
        }
    }

}
