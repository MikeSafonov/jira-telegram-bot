package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class HelpTelegramCommandHandler(
    private val botProperties: BotProperties,
    private val telegramMessageBuilder: TelegramMessageBuilder
) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.INIT) && isMatchText(command, "/help")
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val helpMessage = if (isAdminUser(command.chatId)) {
            """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
/users_list - prints list of users
/add_user <jiraLogin> <telegramId> -  add new user to bot
/remove_user <jiraLogin> - remove user from bot
                    """.trimMargin()
        } else {
            """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
                    """.trimMargin()
        }
        return TelegramCommandResponse(telegramMessageBuilder.createMessage(command.chatId, helpMessage), State.INIT)
    }


    private fun isAdminUser(chatId: Long): Boolean {
        return botProperties.adminId != null && botProperties.adminId == chatId
    }

}