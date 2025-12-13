package ca.hendriks.planningpoker.routing.session

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
    private val sessions = CopyOnWriteArrayList<ServerSSESession>()
    private val mutex = Mutex()

    fun registerSession(session: ServerSSESession) {
        logger.debug { "Tracking new session: ${session.hashCode()}" }
        sessions.add(session)
    }

    fun removeSession(session: ServerSSESession) {
        logger.debug { "Stopped tracking session: ${session.hashCode()}" }
        sessions.remove(session)
    }

    suspend fun broadcastUpdate(myAssignment: Assignment, assignments: Collection<Assignment>) {
        broadcastUpdate(insertRoomFragment(myAssignment, assignments))
    }

    suspend fun broadcastUpdate(data: String) {
        mutex.withLock {
            // Send the data to all active sessions
            for (session in sessions) {
                try {
                    session.send(ServerSentEvent(data, "update"))
                } catch (e: Exception) {
                    // Handle broken connections
                    logger.debug { "Client disconnected from SSE: ${e.message}" }
                    sessions.remove(session)
                }
            }
        }
    }
}
