package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.github.mikesafonov.jira.telegram.config.JiraWatchersProperties
import com.github.mikesafonov.jira.telegram.config.NotificationProperties
import io.atlassian.util.concurrent.Promise
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraWatchersLoaderSpec : BehaviorSpec({
    val clientFactory = mockk<AsynchronousJiraRestClientFactory>()
    val jiraWatchersProperties = mockk<JiraWatchersProperties>()
    val notificationProperties = mockk<NotificationProperties>()
    val issuesClientMock = mockk<IssueRestClient>()
    val jiraClient = mockk<JiraRestClient> {
        every { issueClient } returns issuesClientMock
    }
    every { notificationProperties.jiraUrl } returns "https://some.url"
    every { jiraWatchersProperties.username } returns "username"
    every { jiraWatchersProperties.password } returns "password"

    every {
        clientFactory.createWithBasicHttpAuthentication(
            any(),
            any(),
            any()
        )
    } returns jiraClient

    val jiraWatchersLoader = JiraWatchersLoader(clientFactory, jiraWatchersProperties, notificationProperties)

    Given("Jira watchers loader") {
        When("No watchers") {

            every { issuesClientMock.getWatchers(any()) } returns mockk {
                every { map<List<String>>(any()) } returns mockk<Promise<List<String>>> {
                    every { claim() } returns emptyList()
                }
            }

            Then("Returns empty list") {
                val link = "https://some.link"
                jiraWatchersLoader.getWatchers(link) shouldHaveSize 0
            }
        }

        When("Watchers exists") {
            val users = listOf("user 1", "user 2")
            every { issuesClientMock.getWatchers(any()) } returns mockk {
                every { map<List<String>>(any()) } returns mockk<Promise<List<String>>> {
                    every { claim() } returns users
                }
            }

            Then("Returns empty list") {
                val link = "https://some.link"
                jiraWatchersLoader.getWatchers(link) shouldBe users
            }
        }

        When("Exception was throwed") {
            every { issuesClientMock.getWatchers(any()) } throws RuntimeException()

            Then("Returns empty list") {
                val link = "https://some.link"
                jiraWatchersLoader.getWatchers(link) shouldHaveSize 0
            }
        }
    }

})
