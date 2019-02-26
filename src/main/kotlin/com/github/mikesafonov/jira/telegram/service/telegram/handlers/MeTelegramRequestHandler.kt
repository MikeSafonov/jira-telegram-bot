package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class MeTelegramRequestHandler : TelegramRequestHandler {
    override fun isHandle(command: String): Boolean {
        return command == "/me"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        return SendMessage().apply {
            chatId = id
            text = "Your chat id: $id"
        }
    }

}