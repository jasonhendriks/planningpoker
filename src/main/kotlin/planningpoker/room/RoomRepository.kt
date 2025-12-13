package ca.hendriks.planningpoker.room

class RoomRepository {

    val rooms = mutableMapOf<String, Room>()

    fun findOrCreateRoom(name: String): Room {
        return rooms.getOrElse(name) {
            createRoom(name)
        }
    }

    private fun createRoom(name: String): Room {
        if (rooms.contains(name)) {
            throw IllegalArgumentException("Room with name $name already exists")
        }
        val room = Room(name)
        rooms[name] = room
        return room
    }

}
