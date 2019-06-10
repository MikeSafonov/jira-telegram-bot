package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownCommandTelegramCommandHandler
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class TelegramHandlersHolder(private val handlers: List<TelegramCommandHandler>) {

    fun findHandler(command: TelegramCommand): TelegramCommandHandler {
        return handlers.find { it.isHandle(command) } ?: getUnknownHandler()
    }

    private fun getUnknownHandler(): TelegramCommandHandler {
        return handlers.find { it is UnknownCommandTelegramCommandHandler }!!
    }

}