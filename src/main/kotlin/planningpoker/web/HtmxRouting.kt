package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.CloseVotingCommand
import ca.hendriks.planningpoker.command.JoinRoomCommand
import ca.hendriks.planningpoker.command.LeaveRoomCommand
import ca.hendriks.planningpoker.command.OpenVotingCommand
import ca.hendriks.planningpoker.command.VoteCommand
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.info
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.getOrSet
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun Application.configureHtmxRouting(receiver: CommandReceiver) {

    val logger = LoggerFactory.getLogger("HtmxRouting")

    routing {
        header("HX-Request", "true") {

            route("/assignments") {
                post {
                    val roomName = call.parameters["room-name"]
                    val userName = call.parameters["user-name"]

                    if (roomName == null || roomName.isBlank()) {
                        call.respond(BadRequest, "A Room Name is required")
                        return@post
                    }

                    val user = call.findUserOrCreateUser(userName)
                    val command = JoinRoomCommand(roomName, user, receiver)
                    command.execute()

                    replaceUrl("/room/$roomName")
                    response(command.content)
                }

                route("/{id}") {
                    delete() {
                        logger.info { "HTMX -> Back to Lobby" }

                        call.parameters["id"]?.let { assignmentId ->
                            val userSession: UserSession? = call.sessions.get()
                            val command = LeaveRoomCommand(assignmentId, userSession?.user, receiver)
                            command.execute()

                            replaceUrl("/")
                            response(command.getContent())
                        }
                    }

                    post("/votes/{value}") {

                        val assignmentId = call.parameters["id"]
                        if (assignmentId == null || assignmentId.isBlank()) {
                            throw BadRequestException("An Assignment ID is required")
                        }

                        val value = call.parameters["value"]
                        if (value == null || value.isBlank()) {
                            throw BadRequestException("A Vote Value is required")
                        }

                        call.parameters["id"]?.let { assignmentId ->
                            val command = VoteCommand(value, assignmentId, receiver)
                            command.execute()
                            respondWithNoContent()
                        }
                    }

                }

            }

            route("/room/{room-name}/voting") {
                post {
                    call.parameters["room-name"]
                        ?.let { roomName ->
                            OpenVotingCommand(
                                roomName = roomName,
                                receiver = receiver
                            )
                                .execute()
                            respondWithNoContent()
                        }
                }
                delete {
                    call.parameters["room-name"]
                        ?.let { roomName ->
                            CloseVotingCommand(
                                roomName = roomName,
                                receiver = receiver
                            )
                                .execute()
                            respondWithNoContent()
                        }
                }
            }
        }
    }
}

private fun RoutingContext.respondWithNoContent() = runBlocking {
    call.respond(HttpStatusCode.NoContent, "")
}

private fun RoutingContext.response(content: String) = runBlocking {
    call.respondText(
        text = content,
        contentType = ContentType.Text.Html
    )
}

private fun RoutingContext.replaceUrl(value: String) {
    call.response.headers.append(
        name = "HX-Replace-Url",
        value = value
    )
}

private fun RoutingCall.findUserOrCreateUser(userName: String?): User {
    if (userName != null && !userName.isBlank()) {
        val userSession = this.sessions.getOrSet<UserSession> { UserSession(User()) }
        userSession.user.name = userName
        return userSession.user
    } else {
        val userSession: UserSession? = this.sessions.get()
        require(userSession?.user != null) { "User not found in session" }
        return userSession.user
    }
}
