package ca.hendriks.planningpoker.assignment

import ca.hendriks.planningpoker.room.Room
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.debug
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

class AssignmentRepository {

    private val logger = LoggerFactory.getLogger(AssignmentRepository::class.java)

    private val mappingsByUser = mutableMapOf<User, Assignment>()
    private val mutex = Mutex()

    fun findAssignment(assignmentId: String) = mappingsByUser.values.find { it.id == assignmentId }

    fun findAssignment(user: User?): Assignment? = mappingsByUser[user]

    fun findAssignments(room: Room): Collection<Assignment> =
        mappingsByUser.filterValues { it.room == room }.values

    fun findUsers(room: Room): Collection<User> {
        return mappingsByUser.filterValues { it.room == room }.keys
    }

    suspend fun assignUserToRoom(user: User, room: Room): Assignment {
        mutex.withLock {
            val existingAssignment = mappingsByUser[user]
            if (existingAssignment?.room == room) {
                val assignment = Assignment(user, room, vote = existingAssignment.vote)
                mappingsByUser[user] = assignment
                logger.debug { "Re-Assigned user $user to $room with assignment ID ${assignment.id}" }
                return assignment
            }
            val assignment = Assignment(user, room)
            mappingsByUser[user] = assignment
            logger.debug { "Assigned user $user to $room with assignment ID ${assignment.id}" }
            return assignment
        }
    }

    suspend fun unassign(assignmentId: String) {
        mutex.withLock {
            val assignment = findAssignment(assignmentId)
            if (assignment != null) {
                mappingsByUser.remove(assignment.user)
                logger.debug { "Unassigned user ${assignment.user} from room ${assignment.room}" }
            }
        }
    }

}
