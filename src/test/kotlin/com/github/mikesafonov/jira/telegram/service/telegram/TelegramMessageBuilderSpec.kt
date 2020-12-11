package com.github.mikesafonov.jira.telegram.service.telegram

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage

/**
 * @author Mike Safonov
 */
class TelegramMessageBuilderSpec : BehaviorSpec({
    val builder = TelegramMessageBuilder()

    Given("Builder") {
        When("Simple text message") {
            val id = Arb.long().next()
            val message = Arb.string().next()


            val expectedMessage = SendMessage().apply {
                chatId = id.toString()
                text = message
            }

            Then("Expected messages") {
                builder.createMessages(id, message) shouldContainExactly listOf(expectedMessage)
            }
        }

        When("Big simple text message") {
            val id = Arb.long().next()
            val message = (0 until 2 * TelegramMessageBuilder.maxSize).joinToString("") { "a" }

            Then("Expected messages count") {
                builder.createMessages(id, message) shouldHaveSize 2
            }
        }

        When("Markdown text message") {
            val id = Arb.long().next()
            val message = Arb.string().next()


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
            val id = Arb.long().next()
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
