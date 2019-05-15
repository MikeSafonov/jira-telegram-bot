package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class JiraLoginTelegramCommandHandler(
    private val telegramMessageBuilder: TelegramMessageBuilder
) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.INIT) && isMatchText(command, "/jira_login")
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val jiraId = command.chat!!.jiraId
        return TelegramCommandResponse(telegramMessageBuilder.createMessage(command.chatId, "Your jira login: $jiraId"), State.INIT)
    }
}