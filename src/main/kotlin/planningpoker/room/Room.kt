package ca.hendriks.planningpoker

import ca.hendriks.planningpoker.user.User

class Room(val name: String) {

    private val users = mutableMapOf<String, User>()

    fun addUser(userName: String): User? {
        return if (users.contains(userName)) {
            null
        } else {
            val user = User(userName)
            users[userName] = user
            return user
        }
    }

    fun users() = users.values.toSet()

}
