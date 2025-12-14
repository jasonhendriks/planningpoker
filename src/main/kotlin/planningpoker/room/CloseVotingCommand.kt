package ca.hendriks.planningpoker.room

import ca.hendriks.planningpoker.Receiver
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class CloseVotingCommand(val roomName: String, val receiver: Receiver) {

    private val logger = LoggerFactory.getLogger("WebRouting")

    fun execute() {
        receiver.roomRepository
            .findOrCreateRoom(roomName)
            .closeVoting()
        receiver.broadcastUpdate()
        receiver.respondWithNoContent()
        logger.info { "Voting in room $roomName closed by ${receiver.me?.name}" }
    }

}
