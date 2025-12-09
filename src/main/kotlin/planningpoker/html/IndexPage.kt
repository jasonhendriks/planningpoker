package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Assignment
import ca.hendriks.planningpoker.user.User
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.script

fun HTML.renderIndex(user: User? = null, assignment: Assignment? = null) {
    head {
        link {
            rel = "stylesheet"
            href = "/css/style.css"
        }
        script {
            src = "https://unpkg.com/htmx.org@1.9.10"
        }
        script {
            src = "https://unpkg.com/htmx.org/dist/ext/path-params.js"
        }
        script {
            src = "https://unpkg.com/htmx.org@1.9.10/dist/ext/sse.js"
        }
        script {
            src = "https://unpkg.com/@tailwindcss/browser@4"
        }
    }
    body {
        insertHeader()
        if (assignment == null) {
            div {
                insertJoinRoomForm(user)
            }
        } else {
            insertSseFragment(assignment)
        }
        script {
            src = "/script/error-handling.js"
        }
    }
}

private fun FlowContent.insertHeader() {
    h5 {
        classes =
            setOf("py-8 block font-sans text-xl antialiased font-semibold leading-snug tracking-normal text-blue-gray-900")

        +"Planning Poker"
    }
}
