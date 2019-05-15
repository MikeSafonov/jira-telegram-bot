package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class TelegramCommandExecutor(
    private val holder: TelegramHandlersHolder,
    private val chatRepository: ChatRepository,
    private val telegramMessageBuilder: TelegramMessageBuilder
) {

    fun execute(command: TelegramCommand, body: (BotApiMethod<Message>) -> Unit) {
        val commandResponse = if (command.chat == null) {
            unknownChatResponse(command)
        } else {
            executeByHandler(command)
        }

        updateChatState(command, commandResponse)

        body(commandResponse.method)
    }

    /**
     * Find first handler for [command] and handle it. If no handlers return unknown command response
     */
    private fun executeByHandler(command: TelegramCommand): TelegramCommandResponse {
        val handler = holder.findHandler(command)
        return handler?.handle(command) ?: unknownCommandResponse(command)
    }

    /**
     * If [commandResponse]`s nextState is different to [command]`s chat state then changes chat state in database
     */
    private fun updateChatState(command: TelegramCommand, commandResponse: TelegramCommandResponse) {
        if (command.chat != null && command.chat.state != commandResponse.nextState) {
            command.chat.state = commandResponse.nextState
            chatRepository.save(command.chat)
        }
    }

    private fun unknownChatResponse(command: TelegramCommand): TelegramCommandResponse {
        return TelegramCommandResponse(
            telegramMessageBuilder.createMessage(
                command.chatId,
                "You not registered at this bot yet. Please contact your system administrator for registration."
            ),
            State.INIT
        )
    }

    private fun unknownCommandResponse(command: TelegramCommand): TelegramCommandResponse {
        return TelegramCommandResponse(
            telegramMessageBuilder.createMessage(
                command.chatId,
                "Unknown command. Please use /help to see allowed commands"
            ),
            State.INIT
        )
    }

}