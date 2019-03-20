package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec

/**
 * @author Mike Safonov
 */
class TelegramMessageBuilderSpec : BehaviorSpec({
    val builder = TelegramMessageBuilder()

    Given("Builder") {
        When("") {
            val id = Gen.long().random().first()
            val message = Gen.string().random().first()
            Then("Expected id and text") {
                val sendMessage = builder.createMarkdownMessage(id, message)
                sendMessage.chatId shouldBe id.toString()
                sendMessage.text shouldBe message
            }

            Then("Equals messages") {
                builder.createMarkdownMessage(
                    id,
                    message
                ) shouldBe builder.createMarkdownMessage(id.toString(), message)

                builder.createMessage(id, message) shouldBe builder.createMessage(id.toString(), message)
            }
        }
    }
})