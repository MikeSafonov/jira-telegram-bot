package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.User
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
        jiraRestClientFactory.createRestClient(telegramId).use {
            val jql = JQLBuilder.builder()
                .unresolved()
                .assignedTo(jiraId)
                .orderByDateCreate()
                .build()

            return it.searchClient.searchJql(jql).claim().issues
        }
    }

    /**
     * Call to jira rest API and return issue with given [issueKey].
     * @param telegramId telegram user id
     * @param issueKey jira issue key
     */
    fun getDescription(telegramId: Long, issueKey: String): Issue? {
        jiraRestClientFactory.createRestClient(telegramId).use {
            return it.issueClient
                .getIssue(issueKey)
                .claim()
        }
    }

    fun getMySelf(telegramId: Long): User? {
        jiraRestClientFactory.createMySelfRestClient(telegramId).use {
            return it.getMySelf()
                .claim()
        }
    }

    fun getIssueByFilter(telegramId: Long, filterId: Long): List<Issue> {
        jiraRestClientFactory.createRestClient(telegramId).use { restClient ->
            return restClient
                .searchClient
                .getFilter(filterId)
                .claim()
                ?.let { filter -> return restClient.searchClient.searchJql(filter.jql).claim().issues.toList() }
                ?: emptyList()
        }
    }
}