package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.session.SseSessionManager
import io.ktor.server.sse.ServerSSESession
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

class SseCommand(
    val session: ServerSSESession,
    val assignmentId: String,
    val receiver: CommandReceiver
) : Command {

    private val logger = LoggerFactory.getLogger(SseCommand::class.java)

    override fun execute(): Unit = runBlocking {
        val assignment = receiver.usersToRoom.findAssignment(assignmentId)
        assignment?.let {
            SseSessionManager.addSession(assignment, session)
            receiver.broadcastUpdate(assignment.room)
            try {
                logger.debug { "Client connected to SSE" }
                while (true) {
                    delay(1000)
                    session.send(
                        ServerSentEvent(
                            "",
                            event = "keep-alive"
                        )
                    )
                }
            } finally {
                logger.debug { "Client disconnected from SSE" }
                SseSessionManager.removeSession(session)
                RemoveAssignmentCommand(assignmentId, null, receiver)
                    .execute()
            }
        }
    }

}
