package ca.hendriks.planningpoker.room

class RoomRepository {

    val rooms = mutableMapOf<String, Room>()

    fun findOrCreateRoom(name: String): Room {
        return rooms.getOrElse(name) {
            createRoom(name)
        }
    }

    private fun createRoom(name: String): Room {
        require(!rooms.contains(name)) { "Room with name $name already exists" }
        val room = Room(name)
        rooms[name] = room
        return room
    }

    fun deleteRoom(room: Room) {
        rooms.remove(room.name)
    }

}
