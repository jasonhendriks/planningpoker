package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.room.RoomRepository
import kotlinx.html.*

fun HTML.renderIndex(roomRepository: RoomRepository) {
    head {
        script {
            src = "/assets/htmx.org/dist/htmx.min.js"
        }
        script {
            src = "/assets/htmx.org/dist/ext/path-params.js"
        }
        script {
            src = "/assets/htmx.org/dist/ext/sse.js"
        }
        script {
            src = "https://unpkg.com/@tailwindcss/browser@4"
        }
    }
    body {
        insertHeader()
        val charlie = roomRepository.findRoom("Charlie")
        if (charlie == null) {
            insertJoinRoomForm()
        } else {
            insertRoom(charlie)
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
