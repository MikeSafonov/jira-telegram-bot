package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class MeTelegramRequestHandler : BaseRequestHandler() {
    override fun isHandle(message: Message): Boolean {
        return message.text == "/me"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        return createMessage(id, "Your chat id: $id")
    }

}