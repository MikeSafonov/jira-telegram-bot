package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthApproveTelegramCommandHandler
import com.google.api.client.http.HttpHeaders
import com.google.api.client.http.HttpResponseException
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.*

/**
 * @author Mike Safonov
 */
class JiraAuthApproveTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraAuthService = mockk<JiraAuthService>()
    val telegramClient = mockk<TelegramClient>()

    Given("jira auth approve command handler") {
        val handler = JiraAuthApproveTelegramCommandHandler(jiraAuthService, telegramClient)

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
            val messageIdValue = 1
            val command: TelegramCommand = mockk {
                every { text } returns null
                every { chatId } returns telegramChatId
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { message } returns mockk {
                    every { messageId } returns messageIdValue
                }
            }

            every { jiraAuthService.createTemporaryToken(telegramChatId) } throws RuntimeException()
            every { telegramClient.sendReplaceMessage(any(), any(), any()) } just Runs
            Then("return wrong command syntax") {

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendReplaceMessage(
                        telegramChatId,
                        messageIdValue,
                        "Wrong command syntax\n Should be: <verification code>"
                    )
                }
            }
        }

        When("unable to create access token") {
            val telegramChatId = 1L
            val code = Gen.string().random().first()
            val messageIdValue = 1
            val command: TelegramCommand = mockk {
                every { text } returns code
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { message } returns mockk {
                    every { messageId } returns messageIdValue
                }
            }

            every { jiraAuthService.createAccessToken(telegramChatId, code) } throws RuntimeException()
            every { telegramClient.sendReplaceMessage(any(), any(), any()) } just Runs
            Then("return unexpected error") {

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendReplaceMessage(
                        telegramChatId,
                        messageIdValue,
                        "Unexpected error"
                    )
                }
            }
        }

        When("return 401 status") {
            val telegramChatId = 1L
            val code = Gen.string().random().first()
            val messageIdValue = 1
            val command: TelegramCommand = mockk {
                every { text } returns code
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { message } returns mockk {
                    every { messageId } returns messageIdValue
                }
            }

            val message = "oauth_problem=permission_unknown"
            val exception = HttpResponseException.Builder(401, message, HttpHeaders()).setContent(message).build()

            every { jiraAuthService.createAccessToken(telegramChatId, code) } throws exception
            every { telegramClient.sendReplaceMessage(any(), any(), any()) } just Runs
            Then("return unexpected error") {

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendReplaceMessage(
                        telegramChatId,
                        messageIdValue,
                        "401 $message"
                    )
                }
            }
        }

        When("successful create access token") {
            val telegramChatId = 1L
            val messageIdValue = 1
            val code = Gen.string().random().first()
            val command: TelegramCommand = mockk {
                every { text } returns code
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { message } returns mockk {
                    every { messageId } returns messageIdValue
                }
            }

            every { jiraAuthService.createAccessToken(telegramChatId, code) } returns Unit
            every { telegramClient.sendReplaceMessage(any(), any(), any()) } just Runs
            Then("return authorization success") {
                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendReplaceMessage(
                        telegramChatId,
                        messageIdValue,
                        "Authorization success!"
                    )
                }
            }
        }

    }
})
