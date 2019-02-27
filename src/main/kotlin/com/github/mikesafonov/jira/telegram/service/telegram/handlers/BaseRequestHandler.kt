package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
abstract class BaseRequestHandler : TelegramRequestHandler{
    protected fun createMessage(id: String, message: String): SendMessage {
        return SendMessage().apply {
            chatId = id
            text = message
        }
    }
}