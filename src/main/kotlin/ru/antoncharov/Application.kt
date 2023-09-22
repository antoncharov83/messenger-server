package ru.antoncharov

import io.ktor.server.application.*
import io.ktor.server.netty.*
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
    configureRouting()
    configureSecurity()
    configureSockets()
}

fun Application.createContext() {
    di {
        bindSingleton { createDatasource() }
        bindSingleton { MessageRepository(instance()) }
        bindSingleton { MessageService(instance()) }
    }
}
