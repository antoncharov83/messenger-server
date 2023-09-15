package ru.antoncharov

import kotlinx.datetime.Clock
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Serializable
data class Message(
    @SerialName("_id")
    val id: @Contextual ObjectId? = null,
    val from: Long,
    val to: Long,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val text: String)
