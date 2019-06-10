package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand

/**
 * @author Mike Safonov
 */
abstract class AdminCommandTelegramCommandHandler(
    private val botProperties: BotProperties,
    telegramClient: TelegramClient
) : BaseCommandHandler(telegramClient) {

    override fun isHandle(command: TelegramCommand): Boolean {
        return botProperties.adminId != null && botProperties.adminId == command.chatId
    }
}