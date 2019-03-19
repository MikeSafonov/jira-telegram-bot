package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class JiraAuthCompleteTelegramRequestHandler(val jiraOAuthClient: JiraOAuthClient) : BaseRequestHandler(){
    override fun isHandle(message: Message): Boolean {
        return message.text.startsWith("/auth_complete")
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        val args = message.text.split(" ")
        val accessToken = jiraOAuthClient.getAccessToken(id, args[1])
        return createMessage(id, accessToken)
    }

}