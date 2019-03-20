package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update


private val logger = KotlinLogging.logger {}

/**
 * Service for working with telegram bot api
 * @author Mike Safonov
 */
@Service
class TelegramBot(
    private val botProperties: BotProperties,
    private val telegramMessageBuilder: TelegramMessageBuilder,
    private val chatRepository: ChatRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val telegramCommandExecutor: TelegramCommandExecutor,
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
            val telegramCommand = toCommand(update.message)

            telegramCommandExecutor.execute(telegramCommand) {
                execute(it)
            }
        }
        logger.debug(update.toString())
    }

    private fun toCommand(message: Message): TelegramCommand {
        val telegramId = message.chatId
        val chat = chatRepository.findByTelegramId(telegramId)
        val authorization = authorizationRepository.findById(telegramId).orElse(null)
        return TelegramCommand(message, chat, authorization)
    }

}