package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class HelpTelegramRequestHandler(private val botProperties: BotProperties) : BaseRequestHandler() {
    override fun isHandle(message: Message): Boolean {
        return message.text == "/help"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val helpMessage = getHelpMessage(message)
        return createMessage(message.chatId.toString(), helpMessage)
    }

    private fun getHelpMessage(message: Message): String {
        return if (isAdminUser(message)) {
            """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
/users_list - prints list of users
/add_user <jiraLogin> <telegramId> -  add new user to bot
/remove <jiraLogin> - remove user from bot
                    """.trimMargin()
        } else {
            """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
                    """.trimMargin()
        }

    }

    private fun isAdminUser(message: Message): Boolean {
        return botProperties.adminId != null && botProperties.adminId == message.chatId
    }

}