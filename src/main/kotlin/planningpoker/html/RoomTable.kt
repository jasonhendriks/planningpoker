package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Assignment
import ca.hendriks.planningpoker.user.User
import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.ul

fun FlowContent.insertSseFragment(assignment: Assignment) =
    div {
        id = "room-sse"
        classes = setOf("mx-auto w-full")
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/assignments/${assignment.id}/sse"
        attributes["hx-push-url"] = "true"
        attributes["sse-swap"] = "update"
    }

fun insertRoomFragment(assignment: Assignment, users: Collection<User>) = createHTML()
    .div {

        id = "room"

        h1 {
            +"Welcome to room ${assignment.room.name}"
        }

        form {
            attributes["hx-delete"] = "/assignments/${assignment.id}"
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

            p {
                ul {
                    +"Users in this room:"

                    users.forEach { user ->
                        li {
                            +user.name
                        }
                    }
                }
            }

            div {
                id = "selectable-cards"
                val cards = listOf("1", "2", "3", "5", "8")
                cards.forEach {
                    div {
                        classes = setOf("card")
                        div {
                            classes = setOf("corner top-left")
                            span {
                                +"$it"
                            }
                        }
                    }
                }
            }

        }

    }
