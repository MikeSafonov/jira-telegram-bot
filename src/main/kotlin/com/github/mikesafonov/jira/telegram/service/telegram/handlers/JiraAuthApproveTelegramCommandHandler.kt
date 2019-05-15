package com.github.mikesafonov.jira.telegram.service.telegram.handlers

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
class JiraAuthApproveTelegramCommandHandler(
    private val jiraAuthService: JiraAuthService,
    private val telegramMessageBuilder: TelegramMessageBuilder
) : BaseCommandHandler() {

    override fun isHandle(command: TelegramCommand): Boolean {
        return isInState(command, State.WAIT_APPROVE)
    }

    override fun handle(command: TelegramCommand): TelegramCommandResponse {
        val id = command.chatId
        return if (command.text.isNullOrBlank()) {
            TelegramCommandResponse(telegramMessageBuilder.createMessage(id, "Wrong command syntax\n Should be: <verification code>"), State.INIT)
        } else {
            try {
                jiraAuthService.createAccessToken(id, command.text!!)
                TelegramCommandResponse(telegramMessageBuilder.createMessage(id, "Authorization success!"), State.INIT)
            } catch (e: Exception) {
                logger.error(e.message, e)
                TelegramCommandResponse(telegramMessageBuilder.createMessage(id, "Unexpected error"), State.INIT)
            }
        }
    }

}