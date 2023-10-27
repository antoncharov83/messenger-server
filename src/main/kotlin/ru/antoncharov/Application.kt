package ru.antoncharov

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.di
import ru.antoncharov.plugins.configureRouting
import ru.antoncharov.plugins.configureSerialization
import ru.antoncharov.plugins.createDatasource

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    createContext()
    configureSecurity()
    configureRouting()
    configureSockets()
}

fun Application.createContext() {
    di {
        bindSingleton { createDatasource() }
        bindSingleton { MessageRepository(instance()) }
        bindSingleton { MessageService(instance()) }
        bindSingleton {
            KeycloakBuilder.builder()
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl("localhost:8081")
                .realm("jag-messenger-realm")
                .clientId("jag-messenger-register")
                .clientSecret("Wu1He2nFaW5RvGfRbHht5h5f4c6K9qL8")
                .build()
            }
        bindSingleton { UserService(instance()) }
    }
}
