package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class UsersListTelegramRequestHandler(
    botProperties: BotProperties,
    private val chatRepository: ChatRepository
) :
    AdminCommandTelegramRequestHandler(botProperties) {

    override fun isHandle(message: Message): Boolean {
        return isAdminUser(message) && message.text == "/users_list"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val messageBuilder = StringBuilder("Jira users: \n")
        chatRepository.findAll().forEach {
            messageBuilder.append("- ${it.jiraId}")
        }

        val id = message.chatId.toString()
        return createMessage(id, messageBuilder.toString())
    }
}