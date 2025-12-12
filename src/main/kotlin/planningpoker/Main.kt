package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.routing.configureHtmxRouting
import ca.hendriks.planningpoker.routing.configureRouting
import ca.hendriks.planningpoker.routing.session.UserSession
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sse.SSE
import io.ktor.server.webjars.Webjars
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    LoggerFactory.getLogger("Main").debug { "Started application with args $args" }
    val externalPort = System.getProperty("server.port", "8080").toInt()
    ktor(externalPort)
        .start(wait = true)
}

fun ktor(
    externalPort: Int
): EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration> {
    val roomRepository = RoomRepository()
    val usersToRoom = AssignmentRepository()

    return embeddedServer(Netty, port = externalPort) {
        install(Webjars) {
            path = "assets"
        }
        install(SSE)
        install(Sessions) {
            cookie<UserSession>("user_session")
        }
        configureRouting(roomRepository, usersToRoom)
        configureHtmxRouting(roomRepository, usersToRoom)
    }
}
