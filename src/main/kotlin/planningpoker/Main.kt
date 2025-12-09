package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.routing.UserSession
import ca.hendriks.planningpoker.routing.configureRouting
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.sessions.SessionStorageMemory
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sse.SSE
import io.ktor.server.webjars.Webjars

fun main(args: Array<String>) {
    val externalPort = System.getProperty("server.port", "8888").toInt()
    embeddedServer(Netty, port = externalPort) {
        install(Webjars) {
            path = "assets"
        }
        install(SSE)
        install(Sessions) {
            cookie<UserSession>("user_session", SessionStorageMemory())
        }
        configureRouting()
    }.start(wait = true)
}
