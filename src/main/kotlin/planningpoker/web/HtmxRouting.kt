package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.JoinRoomCommand
import ca.hendriks.planningpoker.command.LeaveRoomCommand
import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.info
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.server.application.Application
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
import org.slf4j.LoggerFactory

fun Application.configureHtmxRouting(receiver: CommandReceiver) {

    val logger = LoggerFactory.getLogger("HtmxRouting")

    routing {
        header("HX-Request", "true") {

            route(LOBBY_PATH) {
                delete("/assignments/{id}") {
                    logger.info { "HTMX -> Back to Lobby" }

                    call.parameters["id"]?.let { assignmentId ->
                        val userSession: UserSession? = call.sessions.get()

                        val command = LeaveRoomCommand(assignmentId, userSession?.user, receiver)
                        command.execute()

                        replaceUrl("/")
                        call.respondText(
                            text = command.getContent(),
                            contentType = ContentType.Text.Html
                        )
                    }
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

                    val user = call.findUserOrCreateUser(userName)
                    val command = JoinRoomCommand(roomName, user, receiver)
                    command.execute()

                    replaceUrl("/room/$roomName")
                    call.respondText(
                        text = command.content,
                        contentType = ContentType.Text.Html
                    )
                }

            }

        }

    }

}

private fun RoutingContext.replaceUrl(value: String) {
    call.response.headers.append(
        name = "HX-Replace-Url",
        value = value
    )
}

private fun RoutingCall.findUserOrCreateUser(userName: String?): User {
    if (userName != null && !userName.trim().isEmpty()) {
        val userSession = this.sessions.getOrSet<UserSession> { UserSession(User()) }
        userSession.user.name = userName
        return userSession.user
    } else {
        val userSession: UserSession? = this.sessions.get()
        require(userSession?.user != null) { "User not found in session" }
        return userSession.user
    }
}
