package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.MeTelegramRequestHandler
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

class MeTelegramRequestHandlerSpec : BehaviorSpec({
    val handler = MeTelegramRequestHandler()

    Given("'/me' telegram command handler") {
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
                        every { text } returns "/me"
                    }) shouldBe true
            }
        }

        When("Message processing") {
            Then("Should return users chat id") {
                val randomId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = randomId.toString()
                    text = "Your chat id: $randomId"
                }

                val message = mockk<Message> {
                    every { chatId } returns randomId
                }

                handler.handle(message) shouldBe expectedMessage
            }
        }
    }
})