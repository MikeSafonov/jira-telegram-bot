package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class NoChatTelegramCommandHandler(telegramClient: TelegramClient) : BaseCommandHandler(telegramClient) {
    override fun isHandle(command: TelegramCommand): Boolean {
        return command.chat == null
    }

    override fun handle(command: TelegramCommand): State {
        telegramClient.sendTextMessage(
            command.chatId,
            "You not registered at this bot yet. Please contact your system administrator for registration."
        )
        return State.INIT
    }
}