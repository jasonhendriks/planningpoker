package ca.hendriks.planningpoker.room

import ca.hendriks.planningpoker.Receiver
import ca.hendriks.planningpoker.util.info
import org.slf4j.LoggerFactory

class OpenVotingCommand(val roomName: String, val receiver: Receiver) {

    private val logger = LoggerFactory.getLogger("WebRouting")

    fun execute() {
        receiver.roomRepository
            .findOrCreateRoom(roomName)
            .openVoting()
        receiver.broadcastUpdate()
        receiver.respondWithNoContent()
        logger.info { "Voting in room $roomName opened by ${receiver.me?.name}" }
    }

}
