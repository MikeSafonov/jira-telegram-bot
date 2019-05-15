package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.MeTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */

class MeTelegramCommandHandlerSpec : BehaviorSpec({

    Given("'/me' telegram command handler") {
        val handler = MeTelegramCommandHandler(TelegramMessageBuilder())
        When("incoming message contain wrong command") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and wrong state") {
            val command: TelegramCommand = mockk {
                every { text } returns "/me"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command") {
            val command: TelegramCommand = mockk {
                every { text } returns "/me"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("Message processing") {
            val randomId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { chatId } returns randomId
            }
            Then("Should return users chat id") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = randomId.toString()
                    text = "Your chat id: $randomId"
                }, State.INIT)


                handler.handle(command) shouldBe expectedMessage
            }
        }
    }
})