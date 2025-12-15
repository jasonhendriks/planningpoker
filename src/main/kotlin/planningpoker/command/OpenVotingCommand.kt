package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class OpenVotingCommand(val roomName: String, val me: User?, val receiver: CommandReceiver) : Command {

    private val logger = LoggerFactory.getLogger(OpenVotingCommand::class.java)

    override fun execute() {
        receiver.roomRepository
            .findOrCreateRoom(roomName)
            .openVoting()
        receiver.broadcastUpdate(me)
        logger.info { "Voting in room $roomName opened by ${me?.name}" }
    }

}
