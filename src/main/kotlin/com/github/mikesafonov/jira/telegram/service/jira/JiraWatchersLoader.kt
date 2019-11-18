package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.mikesafonov.jira.telegram.config.JiraWatchersProperties
import com.github.mikesafonov.jira.telegram.config.NotificationProperties
import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraWatchers
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.URI

private val logger = KotlinLogging.logger {}

/**
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraWatchers
class JiraWatchersLoader(
    clientFactory: AsynchronousJiraRestClientFactory,
    jiraWatchersProperties: JiraWatchersProperties,
    notificationProperties: NotificationProperties
) {
    private val jiraClient: JiraRestClient = clientFactory.createWithBasicHttpAuthentication(
        URI(notificationProperties.jiraUrl),
        jiraWatchersProperties.username,
        jiraWatchersProperties.password
    )

    fun getWatchers(link: String): List<String> {
        try {
            val watchers = jiraClient.issueClient.getWatchers(URI(link))
            return watchers.map {
                it.users.map {
                    it.name
                }
            }.claim()
        } catch (e: Exception) {
            logger.debug(e.message, e)
        }
        return emptyList()
    }
}
