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
        telegramMessageBuilder.createMessage(user, message).also { sender.execute(it) }
    }

    fun sendMarkdownMessage(user: Long, message: String) {
        telegramMessageBuilder.createMarkdownMessage(user, message).also { sender.execute(it) }
    }

    fun sendReplaceMessage(user: Long, idMessage : Int, message : String){
        telegramMessageBuilder.createEditMarkdownMessage(user, idMessage, message).also { sender.execute(it) }
    }
}