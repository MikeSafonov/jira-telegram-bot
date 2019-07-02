package com.github.mikesafonov.jira.telegram.service.parameters

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.generators.EventGen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultParametersBuilderServiceSpec : BehaviorSpec({
    val applicationProperties = mockk<ApplicationProperties>()
    val parametersBuilderService = DefaultParametersBuilderService(applicationProperties)

    Given("DefaultParametersBuilderService with not empty jiraUrl") {
        every { applicationProperties.notification.jiraUrl } returns "http://someurl.com"
        When("Issue is null") {
            val event = EventGen().generateOne(issue = null)
            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com/browse/"
                )
            }
        }
        When("Issue is not null") {
            val event = EventGen.generateDefault()

            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com/browse/${event.issue?.key}"
                )
            }
        }
    }

    Given("DefaultParametersBuilderService with empty jiraUrl") {
        every { applicationProperties.notification.jiraUrl } returns ""
        When("Issue is null") {
            val event = EventGen().generateOne(issue = null)
            Then("Return empty issueLink") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to ""
                )
            }
        }
        When("Issue is not null") {
            val event = EventGen.generateDefault()

            Then("Return issueLink with self") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "${event.issue?.self}"
                )
            }
        }
    }
})