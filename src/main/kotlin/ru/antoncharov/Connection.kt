package ru.antoncharov

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.security.InvalidParameterException
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class Connection(val session: DefaultWebSocketSession)

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val connections = ConcurrentHashMap<String, Connection>()
    val service by closestDI().instance<MessageService>()

    val sessions = ConcurrentHashMap<String, WebSocketServerSession>()

    routing {
            webSocket("/online") {
                val username = JWT.decode(call.request.headers["token"]).getClaim("preferred_username").asString()

                if (username == null){
                    close(CloseReason(CloseReason.Codes.NORMAL, "User not authorized"))
                    return@webSocket
                }

                val to = call.request.headers["to"] ?: throw InvalidParameterException("to must not be null")

                val currentConnection = Connection(this)
                connections[username] = currentConnection

                try {
                    service.getMessageFor(username).forEach { message ->
                        currentConnection.session.send(message.toString())
                    }

                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val toConnection = connections[to]?.session
                        if (toConnection == null) {
                            service.saveMessage(Message(from = username, to = to, text = receivedText))
                            return@webSocket
                        }
                        toConnection.send(Message(from = username, to = to, text = receivedText).toString())
                    }
                } finally {
                    connections.remove(username)
                }
            }
    }
}