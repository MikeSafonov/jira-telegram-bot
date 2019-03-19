package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthClient
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */
@Service
class JiraAuthTelegramRequestHandler(private val jiraOAuthClient: JiraOAuthClient) : BaseRequestHandler() {
    override fun isHandle(message: Message): Boolean {
        return message.text == "/auth"
    }

    override fun handle(message: Message): BotApiMethod<Message> {
        val id = message.chatId.toString()
        val andAuthorizeTemporaryToken = jiraOAuthClient.getAndAuthorizeTemporaryToken(id)
        println(andAuthorizeTemporaryToken)
        val text = """Please allow access [Jira Access](${andAuthorizeTemporaryToken.url})"""
        return createMarkdownMessage(id, text)
    }
}