package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
class TelegramUpdateManager(
    private val chatRepository: ChatRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val holder: TelegramHandlersHolder
) {
    /**
     * process [update]
     */
    fun onUpdate(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val telegramCommand = toCommand(update.message)
            val newState = holder.findHandler(telegramCommand).handle(telegramCommand)
            updateChatState(telegramCommand, newState)
        }
        logger.debug(update.toString())
    }

    private fun toCommand(message: Message): TelegramCommand {
        val telegramId = message.chatId
        val chat = chatRepository.findByTelegramId(telegramId)
        val authorization = authorizationRepository.findById(telegramId).orElse(null)
        return TelegramCommand(message, chat, authorization)
    }

    /**
     * If [nextState] is different to [command]`s chat state then changes chat state in database
     */
    private fun updateChatState(
        command: TelegramCommand,
        nextState: State
    ) {
        if (command.chat != null && command.chat.state != nextState) {
            command.chat.state = nextState
            chatRepository.save(command.chat)
        }
    }
}