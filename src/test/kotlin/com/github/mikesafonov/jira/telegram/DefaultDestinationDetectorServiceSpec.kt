package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.generators.*
import com.github.mikesafonov.jira.telegram.service.destination.DefaultDestinationDetectorService
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultDestinationDetectorServiceSpec : BehaviorSpec({

    val applicationProperties = mockk<ApplicationProperties>()
    val defaultDestinationDetectorService = DefaultDestinationDetectorService(applicationProperties)

    Given("Destination service with flag sendToMe = true") {
        every { applicationProperties.notification.sendToMe } returns true

        When("Event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("Return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("Event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("Return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("Any issue Event") {
            Then("Return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)
                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }

    Given("Destination service with flag sendToMe = false") {
        every { applicationProperties.notification.sendToMe } returns false

        When("Event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("Return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("Event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("Return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("ISSUE_COMMENTED issue Event and comment author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_COMMENTED,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser)),
                comment = CommentGen().generateOne(author = authorUser)
            )
            Then("Return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_CREATED issue Event and event author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_CREATED,
                user = authorUser,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser))
            )
            Then("Return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_UPDATED issue Event and event author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_UPDATED,
                user = authorUser,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser))
            )
            Then("Return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("Issue Event") {
            Then("Return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)

                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }

})