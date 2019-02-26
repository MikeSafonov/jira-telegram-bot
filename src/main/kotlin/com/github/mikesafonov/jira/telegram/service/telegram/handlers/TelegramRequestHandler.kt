package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
interface TelegramRequestHandler {

    fun isHandle(message: Message) : Boolean

    fun handle(message: Message): BotApiMethod<Message>
}