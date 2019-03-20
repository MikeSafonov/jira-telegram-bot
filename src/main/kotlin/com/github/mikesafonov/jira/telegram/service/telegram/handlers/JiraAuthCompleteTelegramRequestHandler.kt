package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class JiraAuthCompleteTelegramRequestHandler(
    private val chatRepository: ChatRepository,
    private val jiraAuthService: JiraAuthService
) : BaseRequestHandler() {

    override fun isHandle(message: Message): Boolean {
        return message.text.startsWith("/auth_complete")
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        val jiraId = chatRepository.findByTelegramId(message.chatId)?.jiraId
        return if (jiraId == null) {
            createMessage(
                id,
                "You not registered at this bot yet. Please contact your system administrator for registration."
            )
        } else {
            val args = message.text.split(" ")
            if (args.size != 2) {
                createMessage(id, "Wrong command syntax\n Should be: /auth_complete <verification code>")
            }
            jiraAuthService.createAccessToken(message.chatId, args[1])
            createMessage(id, "Authorization success!")
        }
    }

}