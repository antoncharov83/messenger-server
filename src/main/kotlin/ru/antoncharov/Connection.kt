package ru.antoncharov

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.security.InvalidParameterException
import java.util.concurrent.ConcurrentHashMap

class Connection(val session: DefaultWebSocketSession, val login: String)

fun Application.configureSockets() {
    val httpClient  = createHttpClient()


    val connections = ConcurrentHashMap<String, Connection>()
    val service by closestDI().instance<MessageService>()

    routing {
        webSocket("/online") {
            val token = call.request.headers["token"] ?: throw InvalidParameterException("token must not be null")
            val userInfo: UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()

            if (userInfo == null) {
                close(CloseReason(CloseReason.Codes.NORMAL, "User not authorized"))
                return@webSocket
            }

            val to = call.request.headers["to"] ?: throw InvalidParameterException("to must not be null")

            val currentConnection = Connection(this, userInfo.id)
            connections[userInfo.id] = currentConnection

            try {
                service.getMessageFor(userInfo.id).forEach { message ->
                    currentConnection.session.send(message.toString())
                }

            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val toConnection = connections[to]?.session
                if (toConnection == null) {
                    service.saveMessage(Message(from = userInfo.id, to = to, text = receivedText))
                    return@webSocket
                }
                toConnection.send(Message(from = userInfo.id, to = to, text = receivedText).toString())
            }
            } finally {
                connections.remove(userInfo.id)
            }
        }
    }
}