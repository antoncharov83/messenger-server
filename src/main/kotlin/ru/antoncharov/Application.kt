package ru.antoncharov

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import ru.antoncharov.plugins.*
import org.kodein.di.ktor.di


fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    createContext()
    configureRouting()
}

fun Application.createContext(){
    di {
        bindSingleton { createDatasource() }
        bindSingleton { MessageRepository(instance()) }
        bindSingleton { MessageService(instance()) }
    }
}
