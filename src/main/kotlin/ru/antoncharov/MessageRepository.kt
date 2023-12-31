package ru.antoncharov

import com.mongodb.client.result.DeleteResult
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MessageRepository(
    private val database: CoroutineDatabase
) {
    suspend fun saveMessage(message: Message) = collection().insertOne(message)

    suspend fun getMessagesForId(to: String): List<Message> {
        return collection().find(Message::to eq to).toList()
    }

    suspend fun deleteMessage(pid: String): DeleteResult {
        val collection = database.getCollection<Message>()
        return collection.deleteOneById(ObjectId(pid))
    }

    private fun collection() =
            database
            .getCollection<Message>()
}