package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class TelegramHandlersHolder(private val handlers: List<TelegramCommandHandler>) {

    fun findHandler(command: TelegramCommand): TelegramCommandHandler? {
        return handlers.find { it.isHandle(command) }
    }

}