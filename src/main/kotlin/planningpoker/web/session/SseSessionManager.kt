package ca.hendriks.planningpoker.web.session

import ca.hendriks.planningpoker.assignment.Assignment
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.html.insertRoomFragment
import io.ktor.server.sse.ServerSSESession
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

object SseSessionManager {

    private val logger = LoggerFactory.getLogger(SseSessionManager::class.simpleName)
    private val sessions = CopyOnWriteArrayList<SseSessionAndPokerAssignment>()
    private val mutex = Mutex()

    fun addSession(assignment: Assignment, session: ServerSSESession) {
        logger.debug { "Tracking new session: ${session.hashCode()}" }
        sessions.add(SseSessionAndPokerAssignment(session, assignment))
    }

    fun removeSession(session: ServerSSESession) {
        logger.debug { "Stopped tracking session: ${session.hashCode()}" }
        sessions.removeIf { it.session == session }
    }

    suspend fun broadcastUpdate(assignments: Collection<Assignment>) {
        broadcastUpdate(assignments, insertRoomFragment())
    }

    suspend fun broadcastUpdate(
        assignments: Collection<Assignment>,
        data: (myAssignment: Assignment, Collection<Assignment>) -> String
    ) {
        mutex.withLock {
            // Send the data to all active sessions
            for (session in sessions) {
                try {
                    val content = data.invoke(session.assignment, assignments)
                    session.session.send(ServerSentEvent(content, "update"))
                } catch (e: Exception) {
                    // Handle broken connections
                    logger.debug { "Client disconnected from SSE: ${e.message}" }
                    sessions.remove(session)
                }
            }
        }
    }
}

data class SseSessionAndPokerAssignment(
    val session: ServerSSESession,
    val assignment: Assignment
)
