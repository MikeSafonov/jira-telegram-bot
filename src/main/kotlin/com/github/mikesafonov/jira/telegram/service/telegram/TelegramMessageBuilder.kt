package com.github.mikesafonov.jira.telegram.service.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

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
}