package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthApproveTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class JiraAuthApproveTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraAuthService = mockk<JiraAuthService>()
    val telegramMessageBuilder = TelegramMessageBuilder()

    Given("jira auth approve command handler") {
        val handler = JiraAuthApproveTelegramCommandHandler(jiraAuthService, telegramMessageBuilder)

        When("incoming message contain wrong state") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right state") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("command without text") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns null
                every { chatId } returns telegramChatId
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }

            every { jiraAuthService.createTemporaryToken(telegramChatId) } throws RuntimeException()

            Then("return wrong command syntax") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text = "Wrong command syntax\n Should be: <verification code>"
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }

        When("unable to crate access token") {
            val telegramChatId = 1L
            val code = Gen.string().random().first()

            val command: TelegramCommand = mockk {
                every { text } returns code
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }

            every { jiraAuthService.createAccessToken(telegramChatId, code) } throws RuntimeException()

            Then("return unexpected error") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text = "Unexpected error"
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }

        When("successful crate access token") {
            val telegramChatId = 1L
            val code = Gen.string().random().first()
            val command: TelegramCommand = mockk {
                every { text } returns code
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }

            every { jiraAuthService.createAccessToken(telegramChatId, code) } returns Unit

            Then("return authorization success") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text = "Authorization success!"
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }

    }
})