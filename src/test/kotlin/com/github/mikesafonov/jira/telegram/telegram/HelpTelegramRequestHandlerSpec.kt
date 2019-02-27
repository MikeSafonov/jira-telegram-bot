package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.HelpTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */

class HelpTelegramRequestHandlerSpec : BehaviorSpec({

    val handler = HelpTelegramRequestHandler()

    Given("'/help' telegram command handler") {
        When("incoming message contain wrong command") {
            Then("isHandle returns false") {
                handler.isHandle(mockk {
                    every { text } returns Gen.string().random().first()
                }) shouldBe false
            }
        }

        When("incoming message contain right command") {
            Then("isHandle returns true") {
                handler.isHandle(
                    mockk {
                        every { text } returns "/help"
                    }) shouldBe true
            }
        }

        When("Message processing") {
            Then("Should return expected help message") {
                val randomChatId = Gen.long().random().first()
                val message = mockk<Message> {
                    every { chatId } returns randomChatId
                }

                val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message""".trimMargin()
                val id = message.chatId
                val expectedMessage = SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }

                handler.handle(message) shouldBe expectedMessage
            }
        }
    }
})