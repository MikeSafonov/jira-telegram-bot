package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.CommentGen
import com.github.mikesafonov.jira.telegram.generators.IssueGen
import com.github.mikesafonov.jira.telegram.generators.UserGen
import com.github.mikesafonov.jira.telegram.service.parameters.DefaultParametersBuilderService
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultParametersBuilderServiceTest : BehaviorSpec({
    val applicationProperties = mockk<ApplicationProperties>()
    val parametersBuilderService = DefaultParametersBuilderService(applicationProperties)

    Given("DefaultParametersBuilderService with not empty jiraUrl") {
        every { applicationProperties.notification.jiraUrl } returns "http://someurl.com"
        When("Issue is null") {
            val event =
                Event(WebHookEvent.COMMENT_CREATED, null, 10000L, UserGen.empty(), IssueGen.empty(), CommentGen.empty(), null)
            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com/browse/"
                )
            }
        }
        When("Issue is not null") {
            val event = Event(
                WebHookEvent.COMMENT_CREATED, null, Gen.long().random().first(), UserGen.empty(),
                IssueGen.generate(), CommentGen.empty(), null
            )

            Then("Return issueLink without key") {
                parametersBuilderService.buildTemplateParameters(event) shouldBe mapOf(
                    "event" to event,
                    "issueLink" to "http://someurl.com/browse/${event.issue?.key}"
                )
            }
        }
    }
})