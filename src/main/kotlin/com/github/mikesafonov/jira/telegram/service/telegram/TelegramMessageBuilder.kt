package com.github.mikesafonov.jira.telegram.service.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage

/**
 * @author Mike Safonov
 */
@Service
class TelegramMessageBuilder {
    companion object {
        const val maxSize = 4096
    }

    fun createMessages(id: Long, message: String): List<SendMessage> {
        return message.chunked(maxSize).map {
            createMessage(id, it)
        }
    }

    fun createMarkdownMessages(id: Long, message: String): List<SendMessage> {
        return message.chunked(maxSize).map {
            createMarkdownMessage(id, it)
        }
    }

    fun createDeleteMessage(id: Long, idMessage: Int): DeleteMessage {
        return DeleteMessage().apply {
            chatId = id.toString()
            messageId = idMessage
        }
    }

    private fun createMessage(id: Long, message: String): SendMessage {
        return createMessage(id.toString(), message)
    }

    private fun createMarkdownMessage(id: Long, message: String): SendMessage {
        return createMarkdownMessage(id.toString(), message)
    }

    private fun createMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            chatId = id
            text = message
        }
    }

    private fun createMarkdownMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            enableMarkdown(true)
            chatId = id
            text = message
        }
    }
}
