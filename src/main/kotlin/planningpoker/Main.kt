package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.routing.UserSession
import ca.hendriks.planningpoker.routing.configureRouting
import ca.hendriks.planningpoker.routing.configureSseRouting
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sse.SSE
import io.ktor.server.webjars.Webjars

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Webjars) {
        path = "assets"
    }
    install(SSE)
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
    configureSseRouting()
    configureRouting()
}
