package ca.hendriks.planningpoker.web.html

import ca.hendriks.planningpoker.assignment.Assignment
import ca.hendriks.planningpoker.user.User
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.hr
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.script

fun HTML.renderIndex(user: User? = null, assignment: Assignment? = null) {
    head {
        link {
            rel = "stylesheet"
            href = "/css/style.css"
        }
        script {
            src = "/assets/htmx.org/2.0.8/dist/htmx.min.js"
        }
        script {
            src = "/assets/htmx-ext-path-params/2.0.0/path-params.js"
        }
        script {
            src = "/assets/htmx-ext-sse/2.2.4/dist/sse.min.js"
        }
        script {
            src = "https://unpkg.com/@tailwindcss/browser@4"
        }
    }
    body {
        insertHeader(user)
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

private fun FlowContent.insertHeader(user: User?) {
    h3 {
        +"Planning Poker"
    }
    if (user != null) {
        p {
            +"Welcome back, ${user.name}!"
        }
    }
    hr { }

}
