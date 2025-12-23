package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.CloseVotingCommand
import ca.hendriks.planningpoker.command.OpenVotingCommand
import io.ktor.server.application.Application
import io.ktor.server.routing.delete
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureHtmxRoomRouting(receiver: CommandReceiver) {

    routing {
        header("HX-Request", "true") {

            route("/room/{room-name}/voting") {
                post {
                    val roomName = call.findRoomNameOrRespondBadRequest()
                    OpenVotingCommand(roomName, receiver)
                        .execute()
                    respondWithNoContent()
                }
                delete {
                    val roomName = call.findRoomNameOrRespondBadRequest()
                    CloseVotingCommand(roomName, receiver)
                        .execute()
                    respondWithNoContent()
                }
            }
        }
    }
}
