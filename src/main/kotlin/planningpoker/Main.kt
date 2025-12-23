package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.assignment.AssignmentRepository
import ca.hendriks.planningpoker.room.RoomRepository
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.ktor
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    LoggerFactory.getLogger("Main").debug { "Started application with args $args" }
    val externalPort = System.getProperty("server.port", "8080").toInt()
    val roomRepository = RoomRepository()
    val usersToRoom = AssignmentRepository()
    ktor(
        port = externalPort,
        roomRepository = roomRepository,
        usersToRoom = usersToRoom
    ).start(wait = true)
}
