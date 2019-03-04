package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramBot
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

/**
 * Main class to process jira event [Event]
 * @author Mike Safonov
 */
@Service
class EventService(
    private val templateService: TemplateService,
    private val destinationDetectorService: DestinationDetectorService,
    private val parametersBuilderService: ParametersBuilderService,
    private val telegramBot: TelegramBot,
    private val chatRepository: ChatRepository
) {

    /**
     *
     */
    fun handle(event: Event) {
        if (event.isIssueEvent) {
            logger.debug { "$event" }
            handleIssue(event)
        } else {
            logger.debug { "Unknown event: $event" }
        }
    }

    /**
     * Handle only issues events.
     */
    private fun handleIssue(event: Event) {
        if (event.issueEventTypeName != null) {
            val destinationLogins = destinationDetectorService.findDestinations(event)
            if (destinationLogins.isNotEmpty()) {
                templateService.buildMessage(event, parametersBuilderService.buildTemplateParameters(event))?.let {
                    sendMessagesToTelegram(destinationLogins, it)
                }
            }
        } else {
            logger.debug { "Event $event not contain issueEventTypeName" }
        }
    }

    /**
     * Send telegram message to jira logins from [destinationLogins]. If no chat id for this
     * login no message will be sended
     * @param destinationLogins list of jira users login
     * @param template message markdown text
     */
    private fun sendMessagesToTelegram(destinationLogins: List<String>, template: CompiledTemplate) {
        destinationLogins.forEach {
            val login = it
            chatRepository.findByJiraId(it)?.let {
                try {
                    telegramBot.sendMarkdownMessage(it.telegramId, template.message)
                } catch (e: Exception) {
                    logger.error("Exception: ${e.message} when sending message to user $login with message: ${template.message}", e)
                }
            }
        }
    }
}