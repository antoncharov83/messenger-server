package ru.antoncharov

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import ru.antoncharov.plugins.UserSession

class MessageController(application: Application) : AbstractDIController(application) {
    private val service by di.instance<MessageService>()

    override fun Route.getRoutes() {
        messages()
    }

    private fun Route.messages() {
        get {
            val userSession = call.sessions.get<UserSession>()

            if (userSession == null) {
                call.respondRedirect("/login")
                return@get
            }
            call.respond(HttpStatusCode.OK, service.getMessageFor(userSession.login))
        }
        post<MessageRequest>() { request ->
            val userSession = call.sessions.get<UserSession>()

            if (userSession == null) {
                call.respondRedirect("/login")
                return@post
            }

            val res = service.saveMessage(Message(from = userSession.login, to = request.to, text = request.text))
            call.respond(HttpStatusCode.OK, res)
        }
        delete("/{id}") {
            val userSession = call.sessions.get<UserSession>()

            if (userSession == null) {
                call.respondRedirect("/login")
                return@delete
            }
            val id = call.parameters.getOrFail("id")
            val res = id.toBigIntegerOrNull(16)?.let { service.messageDelivered(id) } ?: false
            call.respond(HttpStatusCode.OK, res)
        }
    }
}