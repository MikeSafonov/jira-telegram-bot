package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.generators.*
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultDestinationDetectorServiceSpec : BehaviorSpec({


    Given("destination service with flag sendToMe = true") {
        val applicationProperties = mockk<ApplicationProperties>()
        every { applicationProperties.notification.sendToMe } returns true
        val defaultDestinationDetectorService = DefaultDestinationDetectorService(applicationProperties)

        When("event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("any issue Event") {
            Then("return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)
                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }

        When("any issue Event with comment with mention") {
            Then("return list of creator, reporter, assignee and mention names") {
                val mentionName = "mention_login"
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(
                        issueEventTypeName = it,
                        comment = CommentGen().generateOne(body = "[~$mentionName]")
                    )
                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName, mentionName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }

    Given("destination service with flag sendToMe = false") {
        val applicationProperties = mockk<ApplicationProperties>()
        every { applicationProperties.notification.sendToMe } returns false
        val defaultDestinationDetectorService = DefaultDestinationDetectorService(applicationProperties)

        When("event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("ISSUE_COMMENTED issue event and comment author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_COMMENTED,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser)),
                comment = CommentGen().generateOne(author = authorUser)
            )
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_COMMENTED issue event and comment author is null and issue creator exist") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_COMMENTED,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser)),
                comment = CommentGen().generateOne(author = null)
            )
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_CREATED issue event and event author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_CREATED,
                user = authorUser,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser))
            )
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_UPDATED issue event and event author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_UPDATED,
                user = authorUser,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser))
            )
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName)
                val destinations = defaultDestinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("ISSUE_ASSIGNED issue event and event author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_ASSIGNED,
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

        When("issue event") {
            Then("return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)

                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }

        When("any issue event with comment with mention") {
            Then("return list of creator, reporter, assignee and mention names") {
                val mentionName = "mention_login"
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(
                        issueEventTypeName = it,
                        comment = CommentGen().generateOne(body = "[~$mentionName]")
                    )
                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName, mentionName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }

        When("any issue event without comment") {
            Then("return list of creator, reporter, assignee and mention names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(
                        issueEventTypeName = it,
                        comment = null
                    )
                    val expectedNames =
                        listOfNotNull(event.issue?.creatorName, event.issue?.reporterName, event.issue?.assigneeName)
                    val destinations = defaultDestinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }

})
