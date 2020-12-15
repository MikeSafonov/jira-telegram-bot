package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.generators.IssueFieldsGen
import com.github.mikesafonov.jira.telegram.generators.IssueGen
import com.github.mikesafonov.jira.telegram.generators.WatchersGen
import com.github.mikesafonov.jira.telegram.service.jira.JiraWatchersLoader
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class WatchersDestinationDetectorServiceSpec : BehaviorSpec({

    val jiraWatchersLoader = mockk<JiraWatchersLoader>()
    val destinationDetectorService = WatchersDestinationDetectorService(jiraWatchersLoader)

    Given("watchers destination service") {

        When("event without issue field") {
            val event = EventGen().generateOne(issue = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event without watchers") {
            val event = EventGen().generateOne(
                issue = IssueGen().generateOne(
                    issueFields = IssueFieldsGen().generateOne(
                        watchers = null
                    )
                )
            )
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event with issue and watchers") {
            val watcherSelf = Arb.string().next()
            val generatedWatcher = Arb.string().next()
            val event = EventGen().generateOne(
                issue = IssueGen().generateOne(
                    issueFields = IssueFieldsGen().generateOne(
                        watchers = WatchersGen().generateOne(self = watcherSelf)
                    )
                )
            )
            every { jiraWatchersLoader.getWatchers(watcherSelf) } returns listOf(generatedWatcher)
            Then("return list with watcher name") {
                destinationDetectorService.findDestinations(event) shouldBe listOf(generatedWatcher)
            }
        }
    }
})
