package ru.antoncharov

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

class MessageService(
    private val repository: MessageRepository
) {
    suspend fun saveMessage(message: Message): SaveMessageResult {
        val result = runBlocking { repository.saveMessage(message) }
        if(result.wasAcknowledged()) {
            return SaveMessageResult(operationResult = OperationResult.SAVE_MESSAGE_SUCCESS, id = result.insertedId?.asObjectId()?.value?.toHexString())
        }
        return SaveMessageResult(operationResult = OperationResult.SAVE_MESSAGE_FAILED)
    }

    suspend fun getMessageFor(id: Long): List<MessageResponse> {
        return repository.getMessagesForId(id).map { MessageResponse(id = it.id?.toHexString(), from = it.from, text = it.text) }
    }

    suspend fun messageDelivered(id: String): Boolean {
        val result = runBlocking { repository.deleteMessage(id) }
        return result.wasAcknowledged()
    }
}

enum class OperationResult {
    SAVE_MESSAGE_SUCCESS,
    SAVE_MESSAGE_FAILED
}

@Serializable
data class SaveMessageResult(
    val operationResult: OperationResult,
    val id: String? = null
)

@Serializable
data class MessageResponse(val id: String?, val from: Long, val text: String)