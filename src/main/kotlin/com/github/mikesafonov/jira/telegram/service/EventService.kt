package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
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
    private val telegramBotService: TelegramBotService,
    private val templateService: TemplateService,
    private val destinationDetectorService: DestinationDetectorService,
    private val parametersBuilderService: ParametersBuilderService
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
                    val template = it
                    destinationLogins.forEach {
                        telegramBotService.sendMessage(it, template)
                    }
                }
            }
        } else {
            logger.debug { "Event $event not contain issueEventTypeName" }
        }
    }
}