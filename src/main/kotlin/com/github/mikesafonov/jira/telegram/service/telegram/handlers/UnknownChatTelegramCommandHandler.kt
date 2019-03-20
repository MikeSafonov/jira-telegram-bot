package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder

/**
 * @author Mike Safonov
 */
class UnknownChatTelegramCommandHandler(private val telegramMessageBuilder: TelegramMessageBuilder) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return true
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        return TelegramCommandResponse(telegramMessageBuilder.createMessage(
            command.chatId,
            "You not registered at this bot yet. Please contact your system administrator for registration."
        ),
            State.INIT)
    }
}