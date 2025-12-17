package ca.hendriks.planningpoker.web.html

import ca.hendriks.planningpoker.assignment.Assignment
import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.span
import kotlinx.html.stream.createHTML

fun FlowContent.insertSseFragment(assignment: Assignment) =
    div {
        id = "room-sse"
        classes = setOf("mx-auto w-full")
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/assignments/${assignment.id}/sse"
        attributes["sse-swap"] = "update"
    }

fun insertRoomFragment(
): (Assignment, Collection<Assignment>) -> String =
    { myAssignment: Assignment, assignments: Collection<Assignment> ->
        createHTML()
            .div {

                val votingIsInProgress = myAssignment.room.isVotingOpen()
                val votingIsClosed = !votingIsInProgress
                val enableStartVotingButton = !myAssignment.room.isVotingOpen()
                val enableRevealVotesButton = myAssignment.room.isVotingOpen()

                id = "room"

                h1 {
                    +"Welcome to room ${myAssignment.room.name}"
                }

                form {

                    div {
                        classes = setOf("-mx-3 flex flex-wrap")

                        // Leave Room Button
                        div {
                            classes = setOf("w-full px-3 sm:w-1/4 pt-8")
                            button {
                                attributes["hx-delete"] = "/assignments/${myAssignment.id}"
                                attributes["hx-target"] = "#room-sse"
                                attributes["hx-swap"] = "outerHTML"
                                classes =
                                    setOf("bg-slate-800 py-3 px-8 text-center text-base font-semibold text-white outline-none rounded-full cursor-pointer")
                                +"Leave Room"
                            }
                        }

                        // Start Button
                        div {
                            classes = setOf("w-full px-3 sm:w-1/4 pt-8")
                            button {
                                attributes["hx-post"] = "/room/${myAssignment.room.name}/voting"
                                disabled = !enableStartVotingButton
                                var buttonCss =
                                    setOf("bg-slate-800 py-3 px-8 text-center text-base font-semibold text-white outline-none rounded-full")
                                buttonCss = if (enableStartVotingButton) {
                                    buttonCss.plus("cursor-pointer")
                                } else {
                                    buttonCss.plus("opacity-50 cursor-not-allowed")
                                }
                                classes = buttonCss
                                val text = if (myAssignment.room.isVotingNew()) "Start Voting" else "Restart Voting"
                                +text
                            }
                        }

                        // Reveal Votes Button
                        div {
                            classes = setOf("w-full px-3 sm:w-1/4 pt-8")
                            button {
                                attributes["hx-delete"] = "/room/${myAssignment.room.name}/voting"
                                disabled = !enableRevealVotesButton
                                var buttonCss =
                                    setOf("bg-slate-800 py-3 px-8 text-center text-base font-semibold text-white outline-none rounded-full")
                                buttonCss = if (enableRevealVotesButton) {
                                    buttonCss.plus("cursor-pointer")
                                } else {
                                    buttonCss.plus("opacity-50 cursor-not-allowed")
                                }
                                classes = buttonCss
                                +"Reveal Votes"
                            }
                        }
                    }

                }

                div {

                    id = "poker-table"

                    div {
                        id = "user-cards"
                        classes = setOf("card-container")
                        assignments.forEach {
                            div {
                                classes = setOf("card-wrapper")
                                div {
                                    var cardClasses = setOf("card")
                                    if (it.vote != null) {
                                        if (votingIsInProgress) {
                                            cardClasses = cardClasses.plus("face-down")
                                        } else {
                                            cardClasses = cardClasses.plus("face-up")
                                        }
                                    }
                                    classes = cardClasses
                                    // <!-- Card content goes here (e.g., image, text) -->
                                    div {
                                        classes = setOf("")
                                        span {
                                            if (votingIsClosed) {
                                                +it.vote.orEmpty()
                                            }
                                        }
                                    }
                                }
                                div {
                                    classes = setOf("label")
                                    +it.user.name
                                }
                            }
                        }
                    }

                    div {
                        id = "selectable-cards"
                        classes = setOf("card-container")
                        val cards = listOf("1", "2", "3", "5", "8", "13")
                        cards.forEach {
                            div {
                                if (votingIsInProgress)
                                    attributes["hx-post"] = "/assignments/${myAssignment.id}/votes/$it"

                                var cssClasses = setOf("card-wrapper")
                                cssClasses = if (enableStartVotingButton) {
                                    cssClasses.plus("voting-disabled opacity-50 cursor-not-allowed")
                                } else {
                                    cssClasses.plus("voting-enabled")
                                }
                                classes = cssClasses
                                div {
                                    classes = setOf("card face-up")
                                    // <!-- Card content goes here (e.g., image, text) -->
                                    div {
                                        classes = setOf("corner top-left")
                                        span {
                                            +it
                                        }
                                    }
                                }
                                div {
                                    classes = setOf("label")
                                }
                            }
                        }
                    }


                }

            }
    }
