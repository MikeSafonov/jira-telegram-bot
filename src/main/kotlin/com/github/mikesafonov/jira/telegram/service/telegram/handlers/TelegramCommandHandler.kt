package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand

/**
 * @author Mike Safonov
 */
interface TelegramCommandHandler {

    fun isHandle(command: TelegramCommand): Boolean

    fun handle(command: TelegramCommand): State
}