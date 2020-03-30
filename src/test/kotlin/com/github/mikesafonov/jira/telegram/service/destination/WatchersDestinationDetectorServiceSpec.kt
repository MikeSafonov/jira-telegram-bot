package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.generators.*
import com.github.mikesafonov.jira.telegram.service.jira.JiraWatchersLoader
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.string
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class WatchersDestinationDetectorServiceSpec : BehaviorSpec({

    val applicationProperties = mockk<ApplicationProperties>()
    val jiraWatchersLoader = mockk<JiraWatchersLoader>()
    val destinationDetectorService = WatchersDestinationDetectorService(applicationProperties, jiraWatchersLoader)

    Given("destination service with flag sendToMe = true") {
        every { applicationProperties.notification.sendToMe } returns true

        When("event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("any issue Event") {
            Then("return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName,
                            generatedWatcher
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

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
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName,
                            mentionName,
                            generatedWatcher
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }

    Given("destination service with flag sendToMe = false") {
        every { applicationProperties.notification.sendToMe } returns false

        When("event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event without issueEventTypeName field") {
            val event = EventGen().generateOne(issueEventTypeName = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("ISSUE_COMMENTED issue event and comment author and issue creator are same") {
            val authorUser = UserGen.generateDefault()
            val event = EventGen().generateOne(
                issueEventTypeName = IssueEventTypeName.ISSUE_COMMENTED,
                issue = IssueGen().generateOne(issueFields = IssueFieldsGen().generateOne(creator = authorUser)),
                comment = CommentGen().generateOne(author = authorUser)
            )
            val generatedWatcher = Gen.string().random().first()
            every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName, generatedWatcher)
                val destinations = destinationDetectorService.findDestinations(event)

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
            val generatedWatcher = Gen.string().random().first()
            every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(
                        event.issue?.creatorName,
                        event.issue?.reporterName,
                        event.issue?.assigneeName,
                        generatedWatcher
                    )
                val destinations = destinationDetectorService.findDestinations(event)

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
            val generatedWatcher = Gen.string().random().first()
            every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName, generatedWatcher)
                val destinations = destinationDetectorService.findDestinations(event)

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
            val generatedWatcher = Gen.string().random().first()
            every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
            Then("return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName, generatedWatcher)
                val destinations = destinationDetectorService.findDestinations(event)

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
            val generatedWatcher = Gen.string().random().first()
            every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
            Then("Return list of reporter and assignee names") {
                val expectedNames =
                    listOfNotNull(event.issue?.reporterName, event.issue?.assigneeName, generatedWatcher)
                val destinations = destinationDetectorService.findDestinations(event)

                destinations shouldBe expectedNames
            }
        }

        When("issue event") {
            Then("return list of creator, reporter and assignee names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(issueEventTypeName = it)
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)

                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName,
                            generatedWatcher
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

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
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName,
                            mentionName,
                            generatedWatcher
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

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
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName,
                            generatedWatcher
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }

        When("any issue event without comment without watchers") {
            Then("return list of creator, reporter, assignee and mention names") {
                IssueEventTypeName.values().forEach {
                    val event = EventGen().generateOne(
                        issueEventTypeName = it,
                        comment = null,
                        issue = IssueGen().generateOne(
                            issueFields = IssueFieldsGen().generateOne(
                                watchers = null
                            )
                        )
                    )
                    val generatedWatcher = Gen.string().random().first()
                    every { jiraWatchersLoader.getWatchers(any()) } returns listOf(generatedWatcher)
                    val expectedNames =
                        listOfNotNull(
                            event.issue?.creatorName,
                            event.issue?.reporterName,
                            event.issue?.assigneeName
                        )
                    val destinations = destinationDetectorService.findDestinations(event)

                    destinations shouldBe expectedNames
                }
            }
        }
    }
})
