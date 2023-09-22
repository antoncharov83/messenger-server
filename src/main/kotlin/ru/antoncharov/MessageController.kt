package ru.antoncharov

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class MessageController(application: Application) : AbstractDIController(application) {
    private val service by di.instance<MessageService>()

    override fun Route.getRoutes() {
        messages()
    }

    private fun Route.messages() {
        get("/{accountId}") {
            val acc = call.parameters.getOrFail("accountId")
            call.respond(HttpStatusCode.OK, service.getMessageFor(acc))
        }
        post<Message>("") { request ->
            val res = service.saveMessage(request)
            call.respond(HttpStatusCode.OK, res)
        }
        delete("/{id}") {
            val id = call.parameters.getOrFail("id")
            val res = id.toBigIntegerOrNull(16)?.let { service.messageDelivered(id) } ?: false
            call.respond(HttpStatusCode.OK, res)
        }
    }
}