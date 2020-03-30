package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.Issue
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.State
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class JiraApiServiceSpec : BehaviorSpec({
    Given("JiraApiService") {
        When("get my issues called") {
            Then("jira rest client called with expected JQL") {
                val chat = Chat(1, "jira_login", 1L, State.INIT)
                val expectedIssue = emptyList<Issue>()
                val searchRestClient = mockk<SearchRestClient> {
                    every { searchJql(any()) } returns mockk {
                        every { claim().issues } returns expectedIssue
                    }
                }
                val restClient = mockk<JiraRestClient> {
                    every { close() } just Runs
                    every { searchClient } returns searchRestClient
                }
                val jiraRestClientFactory = mockk<JiraRestClientFactory> {
                    every { createRestClient(chat.telegramId) } returns restClient
                }
                val jiraApiService = JiraApiService(jiraRestClientFactory)

                jiraApiService.getMyIssues(chat.telegramId, chat.jiraId) shouldBe expectedIssue

                verify {
                    restClient.close()
                    searchRestClient.searchJql("resolution = Unresolved and assignee = jira_login ORDER BY createdDate")
                }
            }
        }
    }
})
