package ru.antoncharov

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

const val KEYCLOAK_ADDRESS = "http://localhost:8081"

val keycloakProvider = OAuthServerSettings.OAuth2ServerSettings(
    name = "keycloak",
    authorizeUrl = "$KEYCLOAK_ADDRESS/realms/jag-messenger-realm/protocol/openid-connect/auth",
    accessTokenUrl = "$KEYCLOAK_ADDRESS/realms/jag-messenger-realm/protocol/openid-connect/token",
    clientId = "jag-messenger-register",
    clientSecret = "Wu1He2nFaW5RvGfRbHht5h5f4c6K9qL8",
    accessTokenRequiresBasicAuth = false,
    requestMethod = HttpMethod.Post, // must POST to token endpoint
    defaultScopes = listOf("roles")
)

fun Application.configureSecurity() {
    install(Authentication) {
        oauth("auth-jwt") {
            client = HttpClient(CIO)
            providerLookup = { keycloakProvider }
            urlProvider = { "http://localhost:8080/callback" }
        }
    }
}
