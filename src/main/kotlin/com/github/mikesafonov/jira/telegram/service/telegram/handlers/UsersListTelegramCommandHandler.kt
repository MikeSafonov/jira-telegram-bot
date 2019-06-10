package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class UsersListTelegramCommandHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository,
    telegramClient: TelegramClient
) :
    AdminCommandTelegramCommandHandler(botProperties, telegramClient) {

    override fun isHandle(command: TelegramCommand): Boolean {
        return super.isHandle(command) && isInState(command, State.INIT) && isMatchText(command, "/users_list")
    }

    override fun handle(command: TelegramCommand): State {
        val messageBuilder = StringBuilder("Jira users: \n")
        chatRepository.findAll().forEach {
            messageBuilder.append("- ${it.jiraId}\n")
        }

        telegramClient.sendTextMessage(command.chatId, messageBuilder.toString())
        return State.INIT
    }
}