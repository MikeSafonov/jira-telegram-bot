package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.config.JiraBotProperties
import com.github.mikesafonov.jira.telegram.config.NotificationProperties
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.dto.User
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
    private val jiraBotProperties: JiraBotProperties,
    private val templateService: TemplateService
) {

    /**
     *
     */
    fun handle(event: Event) {
        logger.debug { "$event" }
        if (event.isIssueEvent) {
            handleIssue(event)
        } else {
            logger.info { "Unknown event: $event" }
        }
    }

    /**
     * Handle only issues events.
     */
    private fun handleIssue(event: Event) {
        if (event.issueEventTypeName != null) {
            val destinationLogins = findDestinationLogins(event)
            if (destinationLogins.isNotEmpty()) {
                templateService.buildMessage(event, buildTemplateParameters(event))?.let {
                    val template = it
                    destinationLogins.forEach {
                        telegramBotService.sendMessage(it, template)
                    }
                }
            }
        }
    }

    /**
     * Create map of [event] and issue link
     * @param event jira issues event
     * @see buildIssueLink
     * @return template input parameters
     *
     */
    private fun buildTemplateParameters(event: Event): Map<String, Any> {
        val issueLink = buildIssueLink(event)
        return mapOf("event" to event, "issueLink" to issueLink)
    }

    /**
     * Build issue link by concatenation of [NotificationProperties.jiraUrl] and [event.issue.key]. Returning
     * [event.issue.self] if [NotificationProperties.jiraUrl] is `null`
     * @param event jira issues event
     * @return link to browse issue
     */
    private fun buildIssueLink(event: Event): String {
        val notificationProperties = jiraBotProperties.notification
        if (notificationProperties.jiraUrl.isNotBlank()) {
            return if (notificationProperties.jiraUrl.endsWith("/")) {
                "${notificationProperties.jiraUrl}browse/${event.issue?.key}"
            } else {
                "${notificationProperties.jiraUrl}/browse/${event.issue?.key}"
            }
        }
        return event.issue?.self ?: ""
    }

    /**
     * Find jira logins from [event] to send a telegram message
     */
    private fun findDestinationLogins(event: Event): List<String> {
        if (event.issue != null) {
            val notificationProperties = jiraBotProperties.notification
            when (event.issueEventTypeName) {
                IssueEventTypeName.ISSUE_COMMENTED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.comment?.author)
                }
                IssueEventTypeName.ISSUE_CREATED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.user)
                }
                IssueEventTypeName.ISSUE_GENERIC -> {
                    return allIssueUsers(event.issue)
                }
                IssueEventTypeName.ISSUE_UPDATED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.user)
                }
                // TODO: check event dto
                IssueEventTypeName.ISSUE_COMMENT_EDITED -> {
                    return allIssueUsers(event.issue)
                }
                // TODO: check event dto
                IssueEventTypeName.ISSUE_COMMENT_DELETED -> {
                    return allIssueUsers(event.issue)
                }
            }
        }
        return emptyList()
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values
     * @param issue jira issue
     * @return list of logins
     */
    private fun allIssueUsers(issue: Issue): List<String> {
        return listOfNotNull(issue.creatorName, issue.reporterName, issue.assigneeName)
            .distinct()
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values and filtered
     * by [initiator] login if present.
     * @param issue jira issue
     * @param initiator user who fired this issues event
     * @return list of logins
     */
    private fun allIssueUsersWithoutInitiator(issue: Issue, initiator: User?): List<String> {
        return if (initiator == null) {
            allIssueUsers(issue)
        } else {
            allIssueUsers(issue)
                .filter { it != initiator.name }
        }
    }
}