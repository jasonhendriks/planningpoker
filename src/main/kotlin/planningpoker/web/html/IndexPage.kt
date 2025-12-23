package ca.hendriks.planningpoker.web.html

import ca.hendriks.planningpoker.assignment.Assignment
import ca.hendriks.planningpoker.user.User
import kotlinx.html.FlowContent
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.footer
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.hr
import kotlinx.html.html
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import java.io.StringWriter

fun renderIndex(user: User? = null, assignment: Assignment? = null): String {
    return StringWriter().appendHTML()
        .html {
            classes = setOf("h-dvh")

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
                classes = setOf("min-h-screen flex flex-col")
                header {
                    classes = setOf("h-16 bg-gray-600 text-white")
                    insertHeader(user)
                }
                main {
                    classes = setOf("flex-1")
                    if (assignment == null) {
                        div {
                            insertJoinRoomForm(user)
                        }
                    } else {
                        insertSseFragment(assignment)
                    }
                }
                footer {
                    classes = setOf("h-12 bg-gray-800")
                    script {
                        src = "/script/error-handling.js"
                    }
                }
        }
        }.toString()
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
