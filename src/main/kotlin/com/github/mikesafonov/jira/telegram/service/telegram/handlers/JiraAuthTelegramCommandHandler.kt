package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class JiraAuthTelegramCommandHandler(
    private val jiraAuthService: JiraAuthService,
    telegramClient: TelegramClient
) : BaseCommandHandler(telegramClient) {
    override fun isHandle(command: TelegramCommand): Boolean {
        return command.isInState(State.INIT) && command.isMatchText("/auth")
    }

    override fun handle(command: TelegramCommand): State {
        val id = command.chatId
        return try {
            val temporaryToken = jiraAuthService.createTemporaryToken(id)
            val text = """Please allow access [Jira Access](${temporaryToken.url})"""
            telegramClient.sendMarkdownMessage(id, text)
            State.WAIT_APPROVE
        } catch (e: Exception) {
            logger.error(e.message, e)
            telegramClient.sendTextMessage(id, "Unexpected error: unable to create temporary access token")
            State.INIT
        }
    }
}