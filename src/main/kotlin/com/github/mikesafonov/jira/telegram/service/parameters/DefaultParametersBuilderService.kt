package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.jira.JiraIssueBrowseLinkService

/**
 * Default implementation of [ParametersBuilderService]
 * @author Mike Safonov
 */

class DefaultParametersBuilderService(private val jiraIssueBrowseLinkService: JiraIssueBrowseLinkService) : ParametersBuilderService {

    /**
     * Create map from [event] and concatenated issue browse link
     * @see buildIssueLink
     */
    override fun buildTemplateParameters(event: Event): Map<String, Any> {
        return mapOf("event" to event, "issueLink" to buildIssueLink(event))
    }

    private fun buildIssueLink(event: Event): String {
        return jiraIssueBrowseLinkService.createBrowseLink(event.issue?.key, event.issue?.self)
    }

}