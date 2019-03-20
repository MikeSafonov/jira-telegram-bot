package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownChatTelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownCommandTelegramCommandHandler
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class TelegramCommandExecutor(
    private val handlers: List<TelegramCommandHandler>,
    private val chatRepository: ChatRepository,
    private val telegramMessageBuilder: TelegramMessageBuilder
) {

    fun execute(command: TelegramCommand, body: (BotApiMethod<Message>) -> Unit) {
        val commandResponse = if (command.chat == null) {
            UnknownChatTelegramCommandHandler(telegramMessageBuilder).handle(command)
        } else {
            val handler = handlers.find { it.isHandle(command) }
            if (handler == null) {
                UnknownCommandTelegramCommandHandler(telegramMessageBuilder).handle(command)
            } else {
                handler.handle(command)
            }
        }

        if(command.chat != null && command.chat.state != commandResponse.nextState){
            command.chat.state = commandResponse.nextState
            chatRepository.save(command.chat)
        }


        body(commandResponse.method)
    }
}