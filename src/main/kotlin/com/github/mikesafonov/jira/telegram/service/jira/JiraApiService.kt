package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import org.springframework.stereotype.Service

/**
 * This class represents all interaction with Jira REST API
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class JiraApiService(private val jiraRestClientFactory: JiraRestClientFactory) {

    /**
     * Call to jira rest API and return _unresolved_, assigned to [jiraId] issues, ordered by _createdDate_.
     * @param telegramId telegram user id
     * @param jiraId jira id (login)
     */
    fun getMyIssues(telegramId: Long, jiraId: String): Iterable<Issue> {
        val jiraRestClient = jiraRestClientFactory.createRestClient(telegramId)
        val jql = JQLBuilder.builder()
            .unresolved()
            .assignedTo(jiraId)
            .orderByDateCreate()
            .build()

        val issues = jiraRestClient.searchClient.searchJql(jql).claim().issues
        jiraRestClient.close()
        return issues
    }

    /**
     * Call to jira rest API and return issue with given [issueKey].
     * @param telegramId telegram user id
     * @param issueKey jira issue key
     */
    fun getDescription(telegramId: Long, issueKey: String): Issue? {
        return jiraRestClientFactory.createRestClient(telegramId)
            .issueClient
            .getIssue(issueKey)
            .claim()
    }
}