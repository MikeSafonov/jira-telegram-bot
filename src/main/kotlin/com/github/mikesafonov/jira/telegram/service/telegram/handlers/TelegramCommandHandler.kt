package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse

/**
 * @author Mike Safonov
 */
interface TelegramCommandHandler {

    fun isHandle(command: TelegramCommand) : Boolean

    fun handle(command: TelegramCommand): TelegramCommandResponse
}