package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

fun Application.configureSseRouting() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomRepository = RoomRepository()
    roomRepository.createRoom("Charlie")

    routing {
        sse("/sse/sse-{room-name}") {
            SseSessionManager.registerSession(this)
            var eventName = "update"
            try {
                logger.info() { "Client connected to SSE" }
                while (true) {
                    send(ServerSentEvent(data = "", event = eventName))
                    eventName = "keep-alive"
                    delay(1000)
                }
            } finally {
                logger.info() { "Client disconnected from SSE" }
                SseSessionManager.removeSession(this)
            }
        }

    }
}
