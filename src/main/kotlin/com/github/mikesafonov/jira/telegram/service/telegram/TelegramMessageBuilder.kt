package com.github.mikesafonov.jira.telegram.service.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

/**
 * @author Mike Safonov
 */
@Service
class TelegramMessageBuilder {

    fun createMessage(id: Long, message: String): SendMessage {
        return createMessage(id.toString(), message)
    }

    fun createMarkdownMessage(id: Long, message: String): SendMessage {
        return createMarkdownMessage(id.toString(), message)
    }

    fun createMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            chatId = id
            text = message
        }
    }

    fun createMarkdownMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            enableMarkdown(true)
            chatId = id
            text = message
        }
    }

    fun createEditMarkdownMessage(id: Long, idMessage: Int, message: String): EditMessageText {
        return createEditMarkdownMessage(id.toString(), idMessage, message)
    }

    fun createEditMarkdownMessage(id: String, idMessage: Int, message: String): EditMessageText {
        return EditMessageText().apply {
            enableMarkdown(true)
            chatId = id
            text = message
            messageId = idMessage
        }
    }
}