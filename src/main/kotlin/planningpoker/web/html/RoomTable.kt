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
        attributes["hx-push-url"] = "true"
        attributes["sse-swap"] = "update"
    }

fun insertRoomFragment(myAssignment: Assignment, assignments: Collection<Assignment>) = createHTML()
    .div {

        id = "room"

        h1 {
            +"Welcome to room ${myAssignment.room.name}"
        }

        form {
            attributes["hx-delete"] = "/assignments/${myAssignment.id}"
            attributes["hx-target"] = "#room-sse"
            attributes["hx-swap"] = "outerHTML"

            // Button
            div {
                classes = setOf("w-full px-3 sm:w-1/4 pt-8")

                button {
                    classes =
                        setOf("cursor-pointer rounded-md bg-slate-800 py-3 px-8 text-center text-base font-semibold text-white outline-none")

                    +"Leave Room"
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
                            val cardClasses = setOf("card")
                            if (it.vote != null) {
                                cardClasses.plus("face-down")
                            }
                            classes = cardClasses
                            // <!-- Card content goes here (e.g., image, text) -->
                            div {
                                classes = setOf("")
                                span {
                                    +it.vote?.toString().orEmpty()
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
                        classes = setOf("card-wrapper")
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
