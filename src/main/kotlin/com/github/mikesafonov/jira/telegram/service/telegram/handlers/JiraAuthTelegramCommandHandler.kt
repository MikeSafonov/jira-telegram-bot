package com.github.mikesafonov.jira.telegram.service.telegram.handlers

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
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
    private val telegramMessageBuilder: TelegramMessageBuilder
) : BaseCommandHandler() {
    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.INIT) && isMatchText(command, "/auth")
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val id = command.chatId
        return try {
            val temporaryToken = jiraAuthService.createTemporaryToken(id)
            val text = """Please allow access [Jira Access](${temporaryToken.url})"""
            TelegramCommandResponse(telegramMessageBuilder.createMarkdownMessage(id, text), State.WAIT_APPROVE)
        } catch (e: Exception) {
            logger.error(e.message, e)
            TelegramCommandResponse(telegramMessageBuilder.createMessage(id, "Unexpected error: unable to create temporary access token"), State.INIT)
        }
    }
}