package com.github.mikesafonov.jira.telegram.service.jira

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class JiraIssueBrowseLinkServiceSpec : BehaviorSpec({
    val applicationProperties = mockk<ApplicationProperties>()
    val jiraIssueBrowseLinkService = JiraIssueBrowseLinkService(applicationProperties)

    Given("applicationProperties with not empty jiraUrl") {
        When("Key is null") {
            every { applicationProperties.notification.jiraUrl } returns "http://someurl.com"

            Then("Return issueLink without key") {
                jiraIssueBrowseLinkService.createBrowseLink(null, null) shouldBe "http://someurl.com/browse/"
            }
        }
        When("Key is not null") {
            every { applicationProperties.notification.jiraUrl } returns "http://someurl.com/"
            val key = "K1"
            Then("Return issueLink without key") {
                jiraIssueBrowseLinkService.createBrowseLink(key, null) shouldBe "http://someurl.com/browse/$key"
            }
        }
    }

    Given("applicationProperties with empty jiraUrl") {
        every { applicationProperties.notification.jiraUrl } returns ""
        When("Key is null") {
            Then("Return empty issueLink") {
                jiraIssueBrowseLinkService.createBrowseLink(null, null) shouldBe ""
            }
        }
        When("Key is not null but self is null") {

            Then("Return empty issueLink") {
                jiraIssueBrowseLinkService.createBrowseLink("K1", null) shouldBe ""
            }
        }

        When("Key is null but self not null") {
            val self = "self"
            Then("Return issueLink with self") {
                jiraIssueBrowseLinkService.createBrowseLink(null, self) shouldBe self
            }
        }
    }
})
