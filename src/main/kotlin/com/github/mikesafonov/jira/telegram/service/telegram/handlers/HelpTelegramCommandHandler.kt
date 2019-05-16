package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.config.BuildInfo
import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
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
    private val jiraOAuthProperties: JiraOAuthProperties,
    private val telegramMessageBuilder: TelegramMessageBuilder,
    private val buildInfo: BuildInfo
) : BaseCommandHandler() {

    companion object {
        val DEFAULT_HELP_MESSAGE = """
Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
                    """.trimMargin()

        val ADMIN_HELP_MESSAGE = """
Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
/users_list - prints list of users
/add_user <jiraLogin> <telegramId> -  add new user to bot
/remove_user <jiraLogin> - remove user from bot
                    """.trimMargin()
    }


    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.INIT) && isMatchText(command, "/help")
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val helpMessage = buildMessage(command)

        return TelegramCommandResponse(
            telegramMessageBuilder.createMessage(command.chatId, helpMessage),
            State.INIT
        )
    }

    private fun buildMessage(command: TelegramCommand): String {
        val helpMessage = if (isAdminUser(command.chatId)) {
            ADMIN_HELP_MESSAGE
        } else {
            DEFAULT_HELP_MESSAGE
        }

        return if (jiraOAuthProperties.isNotEmpty) {
            """This is jira-telegram-bot v ${buildInfo.version}
$helpMessage
/auth - start jira OAuth
                """.trimMargin()
        } else {
            """This is jira-telegram-bot v ${buildInfo.version}
$helpMessage""".trimIndent()
        }
    }


    private fun isAdminUser(chatId: Long): Boolean {
        return botProperties.adminId != null && botProperties.adminId == chatId
    }

}