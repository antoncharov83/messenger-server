package ru.antoncharov

import com.mongodb.client.result.DeleteResult
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MessageRepository(
    private val database: CoroutineDatabase
) {
    suspend fun saveMessage(message: Message) = collection().insertOne(message)

    suspend fun getMessagesForId(id: Long): List<Message> {
        return collection().find(Message::to eq id).toList()
    }

    suspend fun deleteMessage(pid: String): DeleteResult {
        val collection = database.getCollection<Message>()
        return collection.deleteOneById(pid)
    }

    private fun collection() =
            database
            .getCollection<Message>()
}