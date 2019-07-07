package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class JiraLoginTelegramCommandHandler(
    telegramClient: TelegramClient
) : BaseCommandHandler(telegramClient) {
    override fun isHandle(command: TelegramCommand): Boolean {
        return command.isInState(State.INIT) && command.isMatchText("/jira_login")
    }

    override fun handle(command: TelegramCommand): State {
        val jiraId = command.chat!!.jiraId
        telegramClient.sendTextMessage(command.chatId, "Your jira login: $jiraId")
        return State.INIT
    }
}