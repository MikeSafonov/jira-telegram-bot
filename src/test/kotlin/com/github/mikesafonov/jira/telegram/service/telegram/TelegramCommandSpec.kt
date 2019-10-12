package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.State
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.objects.Message

class TelegramCommandSpec : BehaviorSpec({

    Given("telegram command") {
        When("no chat") {
            val message = mockk<Message>()
            val command = TelegramCommand(
                message, null, null
            )

            Then("") {
                command.isChatExist() shouldBe false
                State.values().forEach {
                    command.isInState(it) shouldBe false
                }
            }
        }
        When("chat exist") {
            val message = mockk<Message>()
            val chat = Chat(1, "jira id", 1L, State.INIT)

            val command = TelegramCommand(
                message, chat, null
            )

            Then("") {
                command.isChatExist() shouldBe true
                command.isInState(State.WAIT_APPROVE) shouldBe false
                command.isInState(State.INIT) shouldBe true
            }
        }

        When("message without text") {
            val idChat = 1L
            val message = mockk<Message> {
                every { hasText() } returns false
                every { text } returns null
                every { chatId } returns idChat
            }
            val command = TelegramCommand(
                message, null, null
            )

            Then("") {
                command.text shouldBe null
                command.hasText shouldBe false
                command.chatId shouldBe idChat
                command.isAnonymous() shouldBe true
                command.isMatchText("some text") shouldBe false
                command.isStartsWithText("some text") shouldBe false
            }
        }

        When("message with text") {
            val idChat = 1L
            val textValue = "/me"
            val message = mockk<Message> {
                every { hasText() } returns true
                every { text } returns textValue
                every { chatId } returns idChat
            }
            val command = TelegramCommand(
                message, null, null
            )

            Then("") {
                command.text shouldBe textValue
                command.hasText shouldBe true
                command.chatId shouldBe idChat
                command.isAnonymous() shouldBe true
                command.isMatchText("some text") shouldBe false
                command.isMatchText(textValue) shouldBe true
                command.isStartsWithText("some text") shouldBe false
                command.isStartsWithText(textValue) shouldBe true
            }
        }
    }
})