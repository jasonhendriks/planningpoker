package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.user.User
import org.slf4j.LoggerFactory

class UserToRoomRepository {

    private val logger = LoggerFactory.getLogger(UserToRoomRepository::class.java)

    private val mapping = mutableMapOf<User, Room>()

    fun assignUserToRoom(user: User, room: Room) {
        mapping[user] = room
        logger.info { "Assigned user $user to $room" }
    }

    fun unassignUser(user: User) {
        val room = mapping.remove(user)
        logger.info { "Unassigned user $user from room $room" }
    }

}
