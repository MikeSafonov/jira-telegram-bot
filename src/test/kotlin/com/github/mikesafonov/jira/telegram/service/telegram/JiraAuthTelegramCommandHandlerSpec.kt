package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraAuthService
import com.github.mikesafonov.jira.telegram.service.jira.oauth.JiraTempTokenAndAuthorizeUrl
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraAuthTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

/**
 * @author Mike Safonov
 */
class JiraAuthTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraAuthService = mockk<JiraAuthService>()
    val telegramClient = mockk<TelegramClient>()

    Given("jira auth command handler") {
        val handler = JiraAuthTelegramCommandHandler(jiraAuthService, telegramClient)

        When("incoming command from anonymous chat") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isAnonymous() } returns true
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("incoming command from not anonymous chat") {

            val command: TelegramCommand = mockk {
                every { isAnonymous() } returns false
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("unable to crate temporary token") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { chat } returns mockk {
                    every { chatId } returns telegramChatId
                    every { state } returns State.INIT
                }
                every { isAnonymous() } returns true
            }

            every { jiraAuthService.createTemporaryToken(telegramChatId) } throws RuntimeException()
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("return unexpected error") {

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        telegramChatId,
                        "Unexpected error: unable to create temporary access token"
                    )
                }
            }
        }

        When("successful crate temporary token") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { chat } returns mockk {
                    every { chatId } returns telegramChatId
                    every { state } returns State.INIT
                }
                every { isAnonymous() } returns true
            }

            val tempToken = JiraTempTokenAndAuthorizeUrl(
                Arb.string().next(),
                Arb.string().next(),
                Arb.string().next()
            )
            every { jiraAuthService.createTemporaryToken(telegramChatId) } returns tempToken
            every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
            Then("return jira access url") {

                handler.handle(command) shouldBe State.WAIT_APPROVE

                verify {
                    telegramClient.sendMarkdownMessage(
                        telegramChatId,
                        "Please allow access [Jira Access](${tempToken.url}) and pass verification code:"
                    )
                }
            }
        }
    }
})
