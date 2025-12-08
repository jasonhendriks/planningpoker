package ca.hendriks.planningpoker.user

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class User @OptIn(ExperimentalUuidApi::class) constructor(val id: Uuid = Uuid.random()) {
    var name: String = ""

    @OptIn(ExperimentalUuidApi::class)
    override fun toString(): String {
        return "User(Name: $name, ID: ${id})"
    }

}
