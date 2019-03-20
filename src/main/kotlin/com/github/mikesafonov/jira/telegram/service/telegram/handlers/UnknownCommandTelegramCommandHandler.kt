package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder

/**
 * @author Mike Safonov
 */
class UnknownCommandTelegramCommandHandler(private val telegramMessageBuilder: TelegramMessageBuilder) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return true
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        return TelegramCommandResponse(
            telegramMessageBuilder.createMessage(
                command.chatId,
                "Unknown command. Please use /help to see allowed commands"
            ),
            State.INIT
        )
    }
}