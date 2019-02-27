package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class JiraLoginTelegramRequestHandler(private val chatRepository: ChatRepository) : BaseRequestHandler() {
    override fun isHandle(message: Message): Boolean {
        return message.text == "/jira_login"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId
        val jiraId = chatRepository.findByTelegramId(id)?.jiraId
        return if (jiraId == null) {
            createMessage(
                id.toString(),
                "You not registered at this bot yet. Please contact your system administrator for registration."
            )
        } else {
            createMessage(id.toString(), "Your jira login: $jiraId")
        }
    }
}