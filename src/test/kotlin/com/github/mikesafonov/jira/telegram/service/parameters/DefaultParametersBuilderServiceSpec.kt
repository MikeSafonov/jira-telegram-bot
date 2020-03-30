package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.service.jira.JiraIssueBrowseLinkService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultParametersBuilderServiceSpec : BehaviorSpec({
    val jiraIssueBrowseLinkService = mockk<JiraIssueBrowseLinkService>()
    val parametersBuilderService = DefaultParametersBuilderService(jiraIssueBrowseLinkService)

    Given("DefaultParametersBuilderService") {
        every {jiraIssueBrowseLinkService.createBrowseLink(any(), any())} returns "http://someurl.com"
        When("Issue is null") {
            val event = EventGen().generateOne(issue = null)
            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com"
                )
            }
        }
        When("Issue is not null") {
            val event = EventGen.generateDefault()
            every {jiraIssueBrowseLinkService.createBrowseLink(event.issue?.key, event.issue?.self)} returns "http://someurl.com/browse/${event.issue?.key}"
            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com/browse/${event.issue?.key}"
                )
            }
        }
    }
})
