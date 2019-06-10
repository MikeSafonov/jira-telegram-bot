package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.google.api.client.http.HttpResponseException
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class JiraAuthApproveTelegramCommandHandler(
    private val jiraAuthService: JiraAuthService,
    telegramClient: TelegramClient
) : BaseCommandHandler(telegramClient) {

    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.WAIT_APPROVE)
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        val messageId = command.message.messageId
        if (command.text.isNullOrBlank()) {
            telegramClient.sendReplaceMessage(id, messageId, "Wrong command syntax\n Should be: <verification code>")
        } else {
            try {
                jiraAuthService.createAccessToken(id, command.text!!)
                telegramClient.sendReplaceMessage(id, messageId,  "Authorization success!")
            } catch (e: HttpResponseException) {
                logger.error(e.message, e)
                val message = "${e.statusCode} ${e.content}"
                telegramClient.sendReplaceMessage(id, messageId,   message)
            } catch (e: Exception) {
                logger.error(e.message, e)
                telegramClient.sendReplaceMessage(id, messageId,   "Unexpected error")
            }
        }

        return State.INIT
    }

}