package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.AuthorizationService
import com.github.mikesafonov.jira.telegram.service.ChatService
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
    private val chatService: ChatService,
    private val authorizationService: AuthorizationService,
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
        val chat = chatService.findByTelegramId(telegramId)
        val authorization = authorizationService.get(telegramId)
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
            chatService.save(command.chat)
        }
    }
}
