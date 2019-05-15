package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraTempTokenAndAuthorizeUrl
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class JiraAuthTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraAuthService = mockk<JiraAuthService>()
    val telegramMessageBuilder = TelegramMessageBuilder()

    Given("jira '/auth' command handler") {
        val handler = JiraAuthTelegramCommandHandler(jiraAuthService, telegramMessageBuilder)

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
                every { text } returns "/auth"
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
                every { text } returns "/auth"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("unable to crate temporary token") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns "/auth"
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            every { jiraAuthService.createTemporaryToken(telegramChatId) } throws RuntimeException()

            Then("return unexpected error") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text = "Unexpected error: unable to create temporary access token"
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }

        When("successful crate temporary token") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns "/auth"
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            val tempToken = JiraTempTokenAndAuthorizeUrl(
                Gen.string().random().first(),
                Gen.string().random().first(),
                Gen.string().random().first()
            )
            every { jiraAuthService.createTemporaryToken(telegramChatId) } returns tempToken

            Then("return jira access url") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text = """Please allow access [Jira Access](${tempToken.url})"""
                    enableMarkdown(true)
                }, State.WAIT_APPROVE)

                handler.handle(command) shouldBe expectedMessage
            }
        }
    }
})