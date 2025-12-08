package ca.hendriks.planningpoker.routing

import ca.hendriks.planningpoker.user.User
import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val user: User)
