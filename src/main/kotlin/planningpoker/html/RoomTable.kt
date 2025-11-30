package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Room
import kotlinx.html.*

fun HTML.renderRoom(room: Room) {
    body {
        insertRoom(room)
    }
}

fun FlowContent.insertRoom(room: Room) {
    h1 {
        +"Welcome to room ${room.name}"
    }
    div {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = "/sse/${room.name}"
        attributes["sse-swap"] = "room"
    }
    // <div hx-ext="sse" sse-connect="/event-source" sse-swap="message"></div>
}
