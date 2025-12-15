package ca.hendriks.planningpoker.command

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.user.User
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class CloseVotingCommand(val roomName: String, val me: User?, val receiver: CommandReceiver) : Command {

    private val logger = LoggerFactory.getLogger(CloseVotingCommand::class.java)

    override fun execute() {
        receiver.roomRepository
            .findOrCreateRoom(roomName)
            .closeVoting()
        receiver.broadcastUpdate(me)
        logger.info { "Voting in room $roomName closed by ${me?.name}" }
    }

}
