package com.github.mikesafonov.jira.telegram.service.telegram

import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class TelegramMessageBuilderSpec : BehaviorSpec({
    val builder = TelegramMessageBuilder()

    Given("Builder") {
        When("simple text message") {
            val id = Gen.long().random().first()
            val message = Gen.string().random().first()


            val expectedMessage = SendMessage().apply {
                chatId = id.toString()
                text = message
            }

            Then("Expected messages") {
                builder.createMessage(id, message) shouldBe expectedMessage
                builder.createMessage(id.toString(), message) shouldBe expectedMessage
            }
        }

        When("markdown text message") {
            val id = Gen.long().random().first()
            val message = Gen.string().random().first()


            val expectedMessage = SendMessage().apply {
                enableMarkdown(true)
                chatId = id.toString()
                text = message
            }

            Then("Expected messages") {
                builder.createMarkdownMessage(id, message) shouldBe expectedMessage
                builder.createMarkdownMessage(id.toString(), message) shouldBe expectedMessage
            }
        }

        When("edit message") {
            val id = Gen.long().random().first()
            val idMessage = Gen.int().random().first()
            val newMessage = Gen.string().random().first()

            Then("Expected messages") {
                var value = builder.createEditMarkdownMessage(id, idMessage, newMessage)
                value.chatId shouldBe id.toString()
                value.messageId shouldBe idMessage
                value.text shouldBe newMessage


                value = builder.createEditMarkdownMessage(id.toString(), idMessage, newMessage)
                value.chatId shouldBe id.toString()
                value.messageId shouldBe idMessage
                value.text shouldBe newMessage
            }
        }
    }
})
