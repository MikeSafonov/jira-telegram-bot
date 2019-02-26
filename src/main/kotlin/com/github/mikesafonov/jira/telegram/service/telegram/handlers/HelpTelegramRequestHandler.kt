package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class HelpTelegramRequestHandler : TelegramRequestHandler {
    override fun isHandle(command: String): Boolean {
        return command == "/help"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
                    """.trimMargin()
        val id = message.chatId
        return SendMessage().apply {
            chatId = id.toString()
            text = helpMessage
        }
    }
}