package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.generators.CommentGen
import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.generators.UserGen
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class EventDestinationServiceSpec : BehaviorSpec({
    Given("event destination service with sendToMe true") {
        val applicationProperties = mockk<ApplicationProperties>()
        val detectors = listOf(
            mockk<DestinationDetectorService>(),
            mockk<DestinationDetectorService>()
        )
        every { applicationProperties.notification.sendToMe } returns true
        val destinationService = EventDestinationService(detectors, applicationProperties)

        When("detectors return logins") {
            val event = EventGen.generateDefault()
            every { detectors[0].findDestinations(event) } returns listOf("one", "two")
            every { detectors[1].findDestinations(event) } returns listOf("one", "three")
            Then("return collected logins") {
                destinationService.findDestinations(event) shouldContainExactlyInAnyOrder setOf("one", "two", "three")
            }
        }


    }

    Given("event destination service with sendToMe false") {
        val applicationProperties = mockk<ApplicationProperties>()
        val detectors = listOf(
            mockk<DestinationDetectorService>(),
            mockk<DestinationDetectorService>()
        )
        every { applicationProperties.notification.sendToMe } returns false
        val destinationService = EventDestinationService(detectors, applicationProperties)

        When("when ISSUE_COMMENTED event") {
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_COMMENTED,
                comment = CommentGen().generateOne(
                    author = UserGen().generateOne(name = "three")
                )
            )
            every { detectors[0].findDestinations(event) } returns listOf("one", "two")
            every { detectors[1].findDestinations(event) } returns listOf("one", "three")
            Then("return collected logins without author") {
                destinationService.findDestinations(event) shouldContainExactlyInAnyOrder setOf("one", "two")
            }
        }

        When("when not ISSUE_COMMENTED event") {

            Then("return collected logins without user") {
                listOf(
                    IssueEventTypeName.ISSUE_CREATED, IssueEventTypeName.ISSUE_GENERIC,
                    IssueEventTypeName.ISSUE_UPDATED, IssueEventTypeName.ISSUE_COMMENT_EDITED,
                    IssueEventTypeName.ISSUE_COMMENT_DELETED, IssueEventTypeName.ISSUE_ASSIGNED
                ).forEach {
                    val event = EventGen().generateOne(
                        issueEventTypeName = it,
                        user = UserGen().generateOne(name = "three")
                    )
                    every { detectors[0].findDestinations(event) } returns listOf("one", "two")
                    every { detectors[1].findDestinations(event) } returns listOf("one", "three")
                    destinationService.findDestinations(event) shouldContainExactlyInAnyOrder setOf("one", "two")
                }
            }
        }
    }
})
