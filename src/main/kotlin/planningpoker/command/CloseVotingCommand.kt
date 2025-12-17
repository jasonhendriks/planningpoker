package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class CloseVotingCommand(val roomName: String, val receiver: CommandReceiver) : Command {

    private val logger = LoggerFactory.getLogger(CloseVotingCommand::class.java)

    override fun execute() {
        val room = receiver.roomRepository.findOrCreateRoom(roomName)
        room.closeVoting()
        receiver.broadcastUpdate(room)
        logger.info { "Voting in room $roomName closed" }
    }

}
