package ru.antoncharov

import kotlinx.serialization.Serializable

class MessageService(
    private val repository: MessageRepository
) {
    suspend fun saveMessage(message: Message): SaveMessageResult {
        val id = repository.saveMessage(message)
        if(id.wasAcknowledged()) {
            return SaveMessageResult(operationResult = OperationResult.SAVE_MESSAGE_SUCCESS, id = message.id.toString())
        }
        return SaveMessageResult(operationResult = OperationResult.SAVE_MESSAGE_FAILED)
    }

    suspend fun getMessageFor(id: Long): List<MessageResponse> {
        val ast = repository.getMessagesForId(id)
        return ast.map { MessageResponse(id = it.id.toString(), from = it.from, text = it.text) }
    }

    suspend fun messageDelivered(id: String): Boolean {
        val re = repository.deleteMessage(id)
        return re.wasAcknowledged()
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
data class MessageResponse(val id: String, val from: Long, val text: String)