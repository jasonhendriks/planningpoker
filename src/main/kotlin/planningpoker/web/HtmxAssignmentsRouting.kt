package ca.hendriks.planningpoker.web

import ca.hendriks.planningpoker.CommandReceiver
import ca.hendriks.planningpoker.command.JoinRoomCommand
import ca.hendriks.planningpoker.command.RemoveAssignmentCommand
import ca.hendriks.planningpoker.command.VoteCommand
import ca.hendriks.planningpoker.util.info
import io.ktor.server.application.Application
import io.ktor.server.routing.delete
import io.ktor.server.routing.header
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

fun Application.configureHtmxAssignmentsRouting(receiver: CommandReceiver) {

    val logger = LoggerFactory.getLogger("HtmxRouting")

    routing {
        header("HX-Request", "true") {

            route("/assignments") {
                post {
                    val roomName = call.findRoomNameOrRespondBadRequest()
                    val user = call.findUserInSessionOrCreateUser()
                    val command = JoinRoomCommand(roomName, user, receiver)
                    command.execute()
                    replaceUrl("/room/$roomName")
                    response(command.content)
                }

                route("/{id}") {
                    delete {
                        logger.info { "HTMX -> Back to Lobby" }
                        val assignmentId = call.findIdOrRespondBadRequest()
                        val user = call.findUserInSession()
                        val command = RemoveAssignmentCommand(assignmentId, user, receiver)
                        command.execute()
                        replaceUrl("/")
                        response(command.content)
                    }

                    post("/votes/{value}") {
                        val assignmentId = call.findIdOrRespondBadRequest()
                        val value = call.findVoteValueOrRespondBadRequest()
                        VoteCommand(value, assignmentId, receiver)
                            .execute()
                        respondWithNoContent()
                    }

                }

            }

        }
    }
}
