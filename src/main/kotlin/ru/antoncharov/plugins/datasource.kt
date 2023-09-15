package ru.antoncharov.plugins


import com.mongodb.MongoClientSettings
import org.bson.codecs.configuration.CodecRegistries
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.service.ClassMappingType

fun createDatasource(): CoroutineDatabase {
    val client = KMongo.createClient().coroutine
    val codecs = ClassMappingType.codecRegistry(MongoClientSettings.getDefaultCodecRegistry())
    return client.getDatabase("message").withCodecRegistry(
        CodecRegistries.fromRegistries(codecs))
}