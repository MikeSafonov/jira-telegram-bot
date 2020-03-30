package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraOAuthAuthenticationHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.net.URI

class JiraRestClientFactorySpec : BehaviorSpec({
    Given("JiraRestClientFactory") {
        When("createRestClient called") {
            Then("return expected restClient") {
                val telegramId = 10L
                val jiraUrl = "http://my.jira.com"
                val uri = URI(jiraUrl)
                val restClient = mockk<JiraRestClient> ()
                val authService = mockk<JiraAuthService> {
                    every { getOAuthParameters(telegramId) } returns mockk {}
                }

                val jiraProperties = mockk<JiraOAuthProperties> {
                    every { baseUrl } returns jiraUrl
                }
                val asynchronousJiraRestClientFactory = mockk<AsynchronousJiraRestClientFactory> {
                    every { createWithAuthenticationHandler(any<URI>(), any<JiraOAuthAuthenticationHandler>()) } returns restClient
                }
                val jiraRestClientFactory =
                    JiraRestClientFactory(authService, jiraProperties, asynchronousJiraRestClientFactory)

                authService.getOAuthParameters(telegramId)

                jiraRestClientFactory.createRestClient(telegramId) shouldBe restClient

                verify {
                    asynchronousJiraRestClientFactory.createWithAuthenticationHandler(
                        uri,
                        any<JiraOAuthAuthenticationHandler>()
                    )
                }
            }
        }
    }
})
