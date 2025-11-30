package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.room.RoomService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.ktor.server.sse.heartbeat
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

fun Application.configureRouting2() {

    val logger = LoggerFactory.getLogger("Routing")
    val roomService = RoomService()
    val roomRepository = RoomRepository()
    roomRepository.createRoom("Charlie")

    routing {
        
        sse("/sse/{room-name}") {
            heartbeat {
                period = 10.milliseconds
                event = ServerSentEvent("heartbeat")
            }
            try {
                logger.info() { "Client subscribed to SSE" }
                repeat(100) {
                    send(ServerSentEvent("this is SSE #$it"))
                    delay(1000)
                }
            } catch (exception: RuntimeException) {
                logger.info { exception }
            }
        }

    }
}
