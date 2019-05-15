package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class UsersListTelegramCommandHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository,
    private val telegramMessageBuilder: TelegramMessageBuilder
) :
    AdminCommandTelegramCommandHandler(botProperties) {

    override fun isHandle(command: TelegramCommand): Boolean {
        return super.isHandle(command) && isInState(command, State.INIT) && isMatchText(command, "/users_list")
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val messageBuilder = StringBuilder("Jira users: \n")
        chatRepository.findAll().forEach {
            messageBuilder.append("- ${it.jiraId}\n")
        }

        return TelegramCommandResponse(telegramMessageBuilder.createMessage(command.chatId, messageBuilder.toString()), State.INIT)
    }
}