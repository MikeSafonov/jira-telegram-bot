package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient

/**
 * @author Mike Safonov
 */
abstract class BaseCommandHandler(protected val telegramClient: TelegramClient) : TelegramCommandHandler {
}