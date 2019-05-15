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
class MeTelegramCommandHandler(private val telegramMessageBuilder: TelegramMessageBuilder) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return isMatchText(command, "/me") && isInState(command, State.INIT)
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val id = command.chatId
        return TelegramCommandResponse(telegramMessageBuilder.createMessage(id, "Your chat id: $id"), State.INIT)
    }

}