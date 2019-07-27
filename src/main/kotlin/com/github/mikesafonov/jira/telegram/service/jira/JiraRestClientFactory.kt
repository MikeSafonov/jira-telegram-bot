package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraOAuth
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthAuthenticationHandler
import org.springframework.stereotype.Service
import java.net.URI

/**
 * Factory for creating Jira Rest API client for specific telegram user
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraOAuth
class JiraRestClientFactory(
    private val authService: JiraAuthService, private val jiraProperties: JiraOAuthProperties,
    private val asynchronousJiraRestClientFactory: AsynchronousJiraRestClientFactory
) {

    fun createRestClient(telegramId: Long): JiraRestClient {
        val oAuthParameters = authService.getOAuthParameters(telegramId)
        return asynchronousJiraRestClientFactory.createWithAuthenticationHandler(
            URI(jiraProperties.baseUrl),
            JiraOAuthAuthenticationHandler(oAuthParameters)
        )
    }
}