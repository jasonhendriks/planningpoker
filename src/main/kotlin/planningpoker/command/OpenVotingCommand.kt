package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.Receiver
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class OpenVotingCommand(val roomName: String, val receiver: Receiver) : Command {

    private val logger = LoggerFactory.getLogger(OpenVotingCommand::class.java)

    override fun execute() {
        receiver.roomRepository
            .findOrCreateRoom(roomName)
            .openVoting()
        receiver.broadcastUpdate()
        receiver.respondWithNoContent()
        logger.info { "Voting in room $roomName opened by ${receiver.me?.name}" }
    }

}
