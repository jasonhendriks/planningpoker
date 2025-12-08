package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Room
import ca.hendriks.planningpoker.routing.LOBBY_PATH
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.stream.createHTML

fun insertSseFragment(room: Room) = createHTML()
    .div {
        id = "room"
        classes = setOf("mx-auto w-full")
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse/sse-${room.name}"
        attributes["hx-push-url"] = "true"
        attributes["sse-swap"] = "update"
    }

fun insertRoomFragment(room: Room) = createHTML()
    .div {

        id = "room-${room.name}"

        h1 {
            +"Welcome to room ${room.name}"
        }

        form {
            attributes["hx-get"] = LOBBY_PATH
            attributes["hx-target"] = "#room"
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

    }
