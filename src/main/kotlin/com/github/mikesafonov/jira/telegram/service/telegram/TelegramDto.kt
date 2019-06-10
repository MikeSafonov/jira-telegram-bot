package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.Authorization
import com.github.mikesafonov.jira.telegram.dao.Chat
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */

data class TelegramCommand(
    val message: Message,
    val chat: Chat?,
    val authorization: Authorization?
) {
    val text: String?
        get() {
            return message.text
        }

    val chatId: Long
        get() {
            return message.chatId
        }

    val hasText: Boolean
        get() {
            return message.hasText()
        }
}