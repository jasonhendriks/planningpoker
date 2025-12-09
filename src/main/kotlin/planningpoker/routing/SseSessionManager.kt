package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.Assignment
import ca.hendriks.planningpoker.html.insertRoomFragment
import ca.hendriks.planningpoker.info
import ca.hendriks.planningpoker.user.User
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
        logger.info { "Tracking new session: ${session.hashCode()}" }
        sessions.add(session)
    }

    fun removeSession(session: ServerSSESession) {
        logger.info { "Stopped tracking session: ${session.hashCode()}" }
        sessions.remove(session)
    }

    suspend fun broadcastUpdate(assignment: Assignment, users: Collection<User>) {
        broadcastUpdate(insertRoomFragment(assignment, users))
    }

    suspend fun broadcastUpdate(data: String) {
        mutex.withLock {
            // Send the data to all active sessions
            for (session in sessions) {
                try {
                    session.send(ServerSentEvent(data, "update"))
                } catch (e: Exception) {
                    // Handle broken connections
                    logger.info { "Client disconnected from SSE: ${e.message}" }
                    sessions.remove(session)
                }
            }
        }
    }
}
