package ru.antoncharov.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = IdKotlinXSerializationModule
            prettyPrint = true
            isLenient = true
        })
    }
}
