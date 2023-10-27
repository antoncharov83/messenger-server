package ru.antoncharov

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import ru.antoncharov.plugins.UserSession

class UsersController(application: Application) : AbstractDIController(application) {
    private val userService by di.instance<UserService>()
    override fun Route.getRoutes() {
        users()
    }

    private fun Route.users(){
        post {
            val userSession = call.sessions.get<UserSession>()

            if (userSession == null) {
                call.respondRedirect("/login")
                return@post
            }

            userService.findByUsername("")
        }
    }
}