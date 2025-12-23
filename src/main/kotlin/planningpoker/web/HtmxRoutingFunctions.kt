package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.routing.session.UserSession
import ca.hendriks.planningpoker.user.User
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.RoutingContext
import io.ktor.server.sessions.get
import io.ktor.server.sessions.getOrSet
import io.ktor.server.sessions.sessions
import kotlinx.coroutines.runBlocking

fun RoutingContext.respondWithNoContent() = runBlocking {
    call.respond(HttpStatusCode.NoContent, "")
}

fun RoutingContext.response(content: String) = runBlocking {
    call.respondText(
        text = content,
        contentType = ContentType.Text.Html
    )
}

fun RoutingContext.replaceUrl(value: String) {
    call.response.headers.append(
        name = "HX-Replace-Url",
        value = value
    )
}

fun RoutingCall.findUserInSession(): User? {
    val userSession: UserSession? = this.sessions.get()
    return userSession?.user
}

fun RoutingCall.findUserInSessionOrCreateUser(): User {
    val userName = findUserName()
    return findUserInSessionOrCreateUser(userName)
}

fun RoutingCall.findUserInSessionOrCreateUser(userName: String?): User {
    return if (userName != null && !userName.isBlank()) {
        val userSession = this.sessions.getOrSet<UserSession> { UserSession(User()) }
        userSession.user.name = userName
        userSession.user
    } else {
        val userSession: UserSession? = this.sessions.get()
        require(userSession?.user != null) { "User not found in session" }
        userSession.user
    }
}

fun RoutingCall.findRoomNameOrRespondBadRequest(): String {
    val roomName = this.parameters["room-name"]
    if (roomName == null || roomName.isBlank()) {
        throw BadRequestException("A Room Name is required")
    }
    return roomName
}

fun ApplicationCall.findIdOrRespondBadRequest(): String {
    val id = this.parameters["id"]
    if (id == null || id.isBlank()) {
        throw BadRequestException("An Assignment ID is required")
    }
    return id
}

fun RoutingCall.findVoteValueOrRespondBadRequest(): String {
    val value = this.parameters["value"]
    if (value == null || value.isBlank()) {
        throw BadRequestException("An Vote Value is required")
    }
    return value
}

fun RoutingCall.findUserName(): String? {
    return this.parameters["user-name"]
}
