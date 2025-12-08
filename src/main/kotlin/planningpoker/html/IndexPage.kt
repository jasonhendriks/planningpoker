package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.Room
import kotlinx.html.BODY
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.script
import kotlinx.html.unsafe

fun HTML.renderIndex(room: Room? = null) {
    head {
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
        if (room == null) {
            div {
                insertJoinRoomForm(null)
            }
        } else {
            insertSseFragment(room)
        }
        insertErrorHandlingScripts()
    }
}

private fun FlowContent.insertHeader() {
    h5 {
        classes =
            setOf("py-8 block font-sans text-xl antialiased font-semibold leading-snug tracking-normal text-blue-gray-900")

        +"Planning Poker"
    }
}

private fun BODY.insertErrorHandlingScripts() {
    script {
        unsafe {
            +"""
            document.body.addEventListener('htmx:responseError', function(evt) {
              alert(evt.detail.xhr.responseText);
            });
            
            document.body.addEventListener('htmx:sendError', function(evt) {
              alert('Server unavailable!');
            });
        """.trimIndent()
        }
    }
}
