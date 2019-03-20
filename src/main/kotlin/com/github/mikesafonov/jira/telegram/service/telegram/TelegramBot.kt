package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramRequestHandler
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update


private val logger = KotlinLogging.logger {}

/**
 * Service for working with telegram bot api
 * @author Mike Safonov
 */
@Service
class TelegramBot(
    private val botProperties: BotProperties,
    private val handlers: List<TelegramRequestHandler>,
    private val telegramMessageBuilder: TelegramMessageBuilder,
    options: DefaultBotOptions?
) :
    TelegramLongPollingBot(options) {

    override fun getBotToken(): String {
        return botProperties.token
    }

    override fun getBotUsername(): String {
        return botProperties.name
    }

    override fun onUpdateReceived(update: Update?) {
        update?.let {
            onUpdate(it)
        }
    }

    fun sendMarkdownMessage(user: Long, message: String) {
        telegramMessageBuilder.createMarkdownMessage(user, message).also { execute(it) }
    }

    /**
     * process [update]
     */
    private fun onUpdate(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val requestHandler = handlers.find { it.isHandle(update.message) }
            if (requestHandler != null) {
                val botApiMethod = requestHandler.handle(update.message)
                execute(botApiMethod)
            } else {
                telegramMessageBuilder.createMessage(update.message.chatId, "Unknown command. Try /help command")
                    .also { execute(it) }
            }
        }
        logger.debug(update.toString())
    }
}