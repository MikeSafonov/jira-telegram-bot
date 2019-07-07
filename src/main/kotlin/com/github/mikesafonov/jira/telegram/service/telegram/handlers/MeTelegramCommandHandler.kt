package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class MeTelegramCommandHandler(telegramClient: TelegramClient) : BaseCommandHandler(telegramClient) {
    override fun isHandle(command: TelegramCommand): Boolean {
        return command.isInState(State.INIT) && command.isMatchText("/me")
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        telegramClient.sendTextMessage(id, "Your chat id: $id")
        return State.INIT
    }

}