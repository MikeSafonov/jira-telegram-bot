package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
abstract class AdminCommandTelegramRequestHandler(private val botProperties: BotProperties) : TelegramRequestHandler {

    protected fun isAdminUser(message : Message) : Boolean{
        return botProperties.adminId != null && botProperties.adminId == message.chat.id
    }

}