package ru.antoncharov.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller
import ru.antoncharov.MessageController

fun Application.configureRouting() {
    routing {
        controller("/api/message") { MessageController(instance()) }
    }
}
