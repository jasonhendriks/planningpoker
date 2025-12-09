package ca.hendriks.planningpoker.user

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(val id: String = Uuid.random().toString()) {
    var name: String = ""

    override fun toString(): String {
        return "User(Name: $name, ID: ${id})"
    }

}
