package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.user.User
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AssignmentRepository {

    private val logger = LoggerFactory.getLogger(AssignmentRepository::class.java)

    private val mappingsByUuid = mutableMapOf<String, Assignment>()
    private val mappingsByUser = mutableMapOf<User, Assignment>()
    private val mutex = Mutex()

    suspend fun assignUserToRoom(user: User, room: Room): Assignment {
        mutex.withLock {
            val assignment = Assignment(user, room)
            mappingsByUuid[assignment.id] = assignment
            mappingsByUser[user] = assignment
            logger.info { "Assigned user $user to $room with assignment ID ${assignment.id}" }
            return assignment
        }
    }

    suspend fun unassign(assignmentId: String) {
        mutex.withLock {
            val assignment = mappingsByUuid.remove(assignmentId)
            if (assignment != null) {
                mappingsByUser.remove(assignment.user)
                logger.info { "Unassigned user ${assignment.user} from room ${assignment.room}" }
            }
        }
    }

    fun findUsersForRoom(room: Room): Collection<User> {
        return mappingsByUser.filterValues { it.room == room }.keys
    }

    fun findAssignment(assignmentId: String) = mappingsByUuid[assignmentId]

    fun findAssignment(user: User?): Assignment? {
        return if (user != null) {
            mappingsByUser[user]
        } else {
            null
        }
    }

}

data class Assignment @OptIn(ExperimentalUuidApi::class) constructor(
    val user: User,
    val room: Room,
    val id: String = Uuid.random().toString()
)
