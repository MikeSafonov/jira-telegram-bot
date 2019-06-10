package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class UnknownCommandTelegramCommandHandler(telegramClient: TelegramClient) : BaseCommandHandler(telegramClient) {
    override fun isHandle(command: TelegramCommand): Boolean {
        return false
    }

    override fun handle(command: TelegramCommand): State {
        telegramClient.sendTextMessage(
            command.chatId,
            "Unknown command. Please use /help to see allowed commands"
        )
        return State.INIT
    }
}