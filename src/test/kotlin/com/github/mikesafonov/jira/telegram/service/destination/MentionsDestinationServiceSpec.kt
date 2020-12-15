package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.generators.CommentGen
import com.github.mikesafonov.jira.telegram.generators.EventGen
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * @author Mike Safonov
 */
class MentionsDestinationServiceSpec : BehaviorSpec({
    Given("mentions destination service") {
        val destinationService = MentionsDestinationService()
        When("event without comment") {
            val event = EventGen().generateOne(comment = null)
            Then("return empty list") {
                destinationService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event with comment without mentions"){
            val event = EventGen().generateOne(comment = CommentGen.generateDefault())
            Then("return empty list") {
                destinationService.findDestinations(event) shouldHaveSize 0
            }
        }

        When("event with comment with mentions"){
            val mentionName = "mention_login"
            val event = EventGen().generateOne(comment = CommentGen().generateOne(body = "[~$mentionName]"))
            Then("return expected mentions list") {
                destinationService.findDestinations(event) shouldBe listOf(mentionName)
            }
        }
    }
})
