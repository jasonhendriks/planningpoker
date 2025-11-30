package ca.hendriks.planningpoker.room

import ca.hendriks.planningpoker.Room

class RoomRepository {

    val rooms = mutableMapOf<String, Room>()

    fun findRoom(name: String): Room? {
        return rooms[name]
    }

    fun createRoom(name: String): Room {
        if (rooms.contains(name)) {
            throw IllegalArgumentException("Room with name $name already exists")
        }
        val room = Room(name)
        rooms.put(name, room)
        return room
    }

}
