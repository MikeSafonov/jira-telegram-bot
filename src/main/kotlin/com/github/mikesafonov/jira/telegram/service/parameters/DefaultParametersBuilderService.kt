package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
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

    override fun buildTemplateParameters(issue: Issue): Map<String, Any> {
        return mapOf("issue" to issue, "issueLink" to buildIssueLink(issue))
    }

    private fun buildIssueLink(event: Event): String {
        return jiraIssueBrowseLinkService.createBrowseLink(event.issue?.key, event.issue?.self)
    }

    private fun buildIssueLink(issue: Issue): String {
        return jiraIssueBrowseLinkService.createBrowseLink(issue.key, issue.self)
    }

}
