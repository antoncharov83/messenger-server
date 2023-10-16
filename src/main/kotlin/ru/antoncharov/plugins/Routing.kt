package ru.antoncharov.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import org.kodein.di.instance
import org.kodein.di.ktor.controller.controller
import ru.antoncharov.MessageController

fun Application.configureRouting() {
    install(Sessions) {
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
    routing {
        controller("/api/message") { MessageController(instance()) }
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }
        authenticate("auth-jwt") {
            get("/callback") {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()

                if (principal != null) {
                    call.sessions.set(UserSession(loggedInSuccessResponse(principal)))
                    call.respondText("Access Token = ${principal.accessToken}")
                } else {
                    call.respondRedirect("/login")
                }
            }
            get(Regex("/|login")) {
                call.respondRedirect("/callback")
            }
        }
    }
}

data class UserSession(val login: String)

private fun loggedInSuccessResponse(
    callback: OAuthAccessTokenResponse.OAuth2
): String {
    val jwtToken = callback.accessToken
    val token = JWT.decode(jwtToken)
    return token.getClaim("preferred_username").asString()
}