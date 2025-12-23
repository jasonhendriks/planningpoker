package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.routing.session.UserSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sse.SSE
import io.ktor.server.webjars.Webjars
import io.ktor.util.cio.ChannelWriteException
import org.slf4j.LoggerFactory

fun ktor(
    port: Int,
    roomRepository: RoomRepository,
    usersToRoom: AssignmentRepository,
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {

    val logger = LoggerFactory.getLogger("WebServerConfiguration")

    return embeddedServer(Netty, port = port) {
        install(Webjars) {
            path = "assets"
        }
        install(SSE)
        install(Sessions) {
            cookie<UserSession>("user_session")
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                if (cause is ChannelWriteException) {
                    logger.info("SSE session closed unexpectedly: ${cause.message}")
                    call.respondText(text = "$cause", status = HttpStatusCode.NoContent)
                } else {
                    logger.error("Unhandled exception", cause)
                    call.respondText(text = "$cause", status = HttpStatusCode.InternalServerError)
                }
            }
        }

        val receiver = CommandReceiver(roomRepository, usersToRoom)
        configureRouting(receiver)
        configureHtmxAssignmentsRouting(receiver)
        configureHtmxRoomRouting(receiver)
    }
}
