package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dto.Event
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.io.StringWriter

private val logger = KotlinLogging.logger {}

@Service
class EventService(
    private val telegramBotService: TelegramBotService,
    private val templateRegistry: TemplateRegistry
) {

    fun handle(event: Event) {
        if (event.isIssueEvent()) {
            handleIssue(event)
        } else {
            logger.info { "Unknown event: $event" }
        }
    }

    private fun handleIssue(event: Event) {
        val mustache = templateRegistry.getByIssueType(event.issueEventTypeName)
        if (mustache != null) {
            val sw = StringWriter()
            mustache.execute(sw, event).flush()
            val telegramMessage = sw.toString()
            //TODO: detect user telegram id
            telegramBotService.sendMessageToUser(0, telegramMessage)
        } else {
            logger.info { "Template for event $event not found" }
        }
    }


}