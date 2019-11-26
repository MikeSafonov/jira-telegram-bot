package com.github.mikesafonov.jira.telegram.service.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultAbsSender

/**
 * @author Mike Safonov
 */
@Service
class TelegramClient(
    private val sender: DefaultAbsSender,
    private val telegramMessageBuilder: TelegramMessageBuilder
) {

    fun sendTextMessage(user: Long, message: String) {
        telegramMessageBuilder.createMessages(user, message).forEach { sender.execute(it) }
    }

    fun sendMarkdownMessage(user: Long, message: String) {
        telegramMessageBuilder.createMarkdownMessages(user, message).forEach { sender.execute(it) }
    }

    fun sendDeleteMessage(user : Long, idMessage: Int){
        telegramMessageBuilder.createDeleteMessage(user, idMessage).also { sender.execute(it) }
    }
}
