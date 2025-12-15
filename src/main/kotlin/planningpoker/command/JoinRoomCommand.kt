package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.debug
import ca.hendriks.planningpoker.web.html.insertSseFragment
import kotlinx.coroutines.runBlocking
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import org.slf4j.LoggerFactory

class JoinRoomCommand(val roomName: String, val user: User, val receiver: CommandReceiver) : Command {

    val logger = LoggerFactory.getLogger(JoinRoomCommand::class.java)
    lateinit var content: String

    override fun execute() = runBlocking {
        val room = receiver.roomRepository.findOrCreateRoom(roomName)
        logger.debug { "Found/Created $user in the session" }
        val assignment = receiver.usersToRoom.assignUserToRoom(user, room)

        content = createHTML().div { insertSseFragment(assignment) }
    }
}
