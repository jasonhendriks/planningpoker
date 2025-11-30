package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Room
import kotlinx.html.*

fun HTML.renderSse(room: Room) {
    body {
        insertSse(room)
    }
}

fun FlowContent.insertSse(room: Room) {
    div {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse/sse-${room.name}"

        div {
            attributes["hx-get"] = "/rooms/${room.name}"
            attributes["hx-trigger"] = "sse:update"
        }
    }
}

fun HTML.renderRoom(room: Room) {
    body {
        insertRoom(room)
    }
}

fun FlowContent.insertRoom(room: Room) {
    div {
        id = "main"
        classes = setOf("mx-auto w-full")

        h1 {
            +"Welcome to room ${room.name}"
        }

        form {
            attributes["hx-get"] = "/rooms"
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
}
