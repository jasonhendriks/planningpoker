package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.Receiver
import ca.hendriks.planningpoker.routing.session.SseSessionManager
import ca.hendriks.planningpoker.util.debug
import io.ktor.server.sse.ServerSSESession
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class SseCommand(
    val session: ServerSSESession,
    val assignmentId: String,
    val receiver: Receiver
) : Command {

    private val logger = LoggerFactory.getLogger(SseCommand::class.java)

    override fun execute(): Unit = runBlocking {
        val assignment = receiver.usersToRoom.findAssignment(assignmentId)
        assignment?.let {
            SseSessionManager.registerSession(session)
            receiver.broadcastUpdate()
            try {
                logger.debug { "Client connected to SSE" }
                while (true) {
                    session.send(
                        ServerSentEvent(
                            "",
                            event = "keep-alive"
                        )
                    )
                    delay(1000)
                }
            } finally {
                logger.debug { "Client disconnected from SSE" }
                SseSessionManager.removeSession(session)
                receiver.usersToRoom.unassign(assignmentId)
                receiver.broadcastUpdate()
            }
        }
    }

}
