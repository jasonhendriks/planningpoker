package ca.hendriks.planningpoker.html

import ca.hendriks.planningpoker.user.User
import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label

fun FlowContent.insertJoinRoomForm(user: User?) =
    div {
        id = "room-form"
        classes = setOf("mx-auto w-full")

        form {
            if (user == null) {
                attributes["hx-post"] = "/assignments/{room-name}?user-name={user-name}"
            } else {
                attributes["hx-post"] = "/assignments/{room-name}"
            }
            attributes["hx-ext"] = "path-params"
            attributes["hx-target"] = "#room-form"
            attributes["hx-swap"] = "outerHTML"

            div {
                classes = setOf("-mx-3 flex flex-wrap")

                // Room Name
                div {
                    classes = setOf("w-full px-3 sm:w-1/4")

                    label {
                        classes = setOf("mb-3 block text-base font-medium text-[#07074D]")

                        htmlFor = "room-name"
                        +"Room Name"
                    }
                    input {
                        classes =
                            setOf("w-full rounded-md border border-[#e0e0e0] bg-white py-3 px-6 text-base font-medium text-[#6B7280] outline-none focus:border-[#6A64F1] focus:shadow-md")

                        type = InputType.text
                        name = "room-name"
                        id = "room-name"
                        placeholder = "Room Name"
                        value = "Charlie"
                    }
                }

                if (user == null) {
                    // User Name
                    div {
                        classes = setOf("w-full px-3 sm:w-1/4")

                        label {
                            classes = setOf("mb-3 block text-base font-medium text-[#07074D]")

                            htmlFor = "user-name"
                            +"User Name"
                        }
                        input {
                            classes =
                                setOf("w-full rounded-md border border-[#e0e0e0] bg-white py-3 px-6 text-base font-medium text-[#6B7280] outline-none focus:border-[#6A64F1] focus:shadow-md")

                            type = InputType.text
                            name = "user-name"
                            id = "user-name"
                            placeholder = "User Name"
                            value = "Jason"
                        }
                    }
                }

                // Button
                div {
                    classes = setOf("w-full px-3 sm:w-1/4 pt-8")

                    button {
                        classes =
                            setOf("cursor-pointer rounded-md bg-slate-800 py-3 px-8 text-center text-base font-semibold text-white outline-none")

                        +"Join Room"
                    }
                }
            }
        }
    }
