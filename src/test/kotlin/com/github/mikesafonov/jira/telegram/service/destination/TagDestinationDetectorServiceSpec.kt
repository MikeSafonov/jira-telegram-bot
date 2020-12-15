package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.generators.CommentGen
import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.TagService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class TagDestinationDetectorServiceSpec : BehaviorSpec({
    Given("tag destination detector service") {
        val tagService = mockk<TagService>()
        val chatService = mockk<ChatService>()
        val destinationDetectorService = TagDestinationDetectorService(tagService, chatService)

        When("event without comment") {
            val event = EventGen().generateOne(comment = null)
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event with comment without tags") {
            val event = EventGen().generateOne(comment = CommentGen().generateOne(body = "simple message without tags"))
            Then("return empty list") {
                destinationDetectorService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event with comment with everyone tag") {
            val event = EventGen().generateOne(comment = CommentGen().generateOne(body = "hello @everyone"))
            val expected = listOf("one", "two")
            every { chatService.getAllLogins() } returns expected
            Then("return all logins") {
                destinationDetectorService.findDestinations(event) shouldBe expected
            }
        }

        When("event with comment several tags") {
            val event = EventGen().generateOne(comment = CommentGen().generateOne(body = "hello @devs and @qa"))
            val expected = listOf("one", "two", "three")
            every { tagService.getJiraLoginsByTagKey("qa") } returns listOf("one")
            every { tagService.getJiraLoginsByTagKey("devs") } returns listOf("two", "three")
            Then("return devs and qa logins") {
                destinationDetectorService.findDestinations(event) shouldContainExactlyInAnyOrder expected
            }
        }
    }
})
