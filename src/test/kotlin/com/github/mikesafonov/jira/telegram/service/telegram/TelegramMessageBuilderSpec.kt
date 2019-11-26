package com.github.mikesafonov.jira.telegram.service.telegram

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage

/**
 * @author Mike Safonov
 */
class TelegramMessageBuilderSpec : BehaviorSpec({
    val builder = TelegramMessageBuilder()

    Given("Builder") {
        When("Simple text message") {
            val id = Gen.long().random().first()
            val message = Gen.string().random().first()


            val expectedMessage = SendMessage().apply {
                chatId = id.toString()
                text = message
            }

            Then("Expected messages") {
                builder.createMessages(id, message) shouldContainExactly listOf(expectedMessage)
            }
        }

        When("Big simple text message") {
            val id = Gen.long().random().first()
            val message = (0 until 2 * TelegramMessageBuilder.maxSize).joinToString("") { "a" }

            Then("Expected messages count") {
                builder.createMessages(id, message) shouldHaveSize 2
            }
        }

        When("Markdown text message") {
            val id = Gen.long().random().first()
            val message = Gen.string().random().first()


            val expectedMessage = SendMessage().apply {
                enableMarkdown(true)
                chatId = id.toString()
                text = message
            }

            Then("Expected messages") {
                builder.createMarkdownMessages(id, message) shouldContainExactly listOf(expectedMessage)
            }
        }

        When("Big markdown text message") {
            val id = Gen.long().random().first()
            val message = (0 until 2 * TelegramMessageBuilder.maxSize).joinToString("") { "a" }

            Then("Expected messages count") {
                builder.createMarkdownMessages(id, message) shouldHaveSize 2
            }
        }

        When("Delete message") {
            val id = 100L
            val message = 1

            val expectedDeleteMessage = DeleteMessage().apply {
                chatId = id.toString()
                messageId = message
            }

            Then("Expected delete message") {
                val deleteMessage = builder.createDeleteMessage(id, message)
                deleteMessage.chatId shouldBe expectedDeleteMessage.chatId
                deleteMessage.messageId shouldBe expectedDeleteMessage.messageId
            }
        }
    }
})
