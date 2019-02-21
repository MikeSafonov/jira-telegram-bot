package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.config.NotificationProperties
import com.github.mikesafonov.jira.telegram.dto.Event

/**
 * Default implementation of [ParametersBuilderService]
 * @author Mike Safonov
 */

class DefaultParametersBuilderService(private val applicationProperties: ApplicationProperties) : ParametersBuilderService {

    /**
     * Create map from [event] and concatenated issue browse link
     * @see buildIssueLink
     */
    override fun buildTemplateParameters(event: Event): Map<String, Any> {
        return mapOf("event" to event, "issueLink" to buildIssueLink(event))
    }

    /**
     * Build issue link by concatenation of [NotificationProperties.jiraUrl] and [event.issue.key]. Returning
     * [event.issue.self] if [NotificationProperties.jiraUrl] is `null`
     * @param event jira issues event
     * @return link to browse issue
     */
    private fun buildIssueLink(event: Event): String {
        val notificationProperties = applicationProperties.notification
        if (notificationProperties.jiraUrl.isNotBlank()) {
            return if (notificationProperties.jiraUrl.endsWith("/")) {
                "${notificationProperties.jiraUrl}browse/${event.issue?.key}"
            } else {
                "${notificationProperties.jiraUrl}/browse/${event.issue?.key}"
            }
        }
        return event.issue?.self ?: ""
    }

}