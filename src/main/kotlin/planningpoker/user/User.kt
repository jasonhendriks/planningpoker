package ca.hendriks.planningpoker.user

import ca.hendriks.planningpoker.util.uuidSupplier
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String = uuidSupplier().invoke()) {

    var name: String = ""

    override fun toString(): String {
        return "User(Name: $name, ID: ${id})"
    }

}
