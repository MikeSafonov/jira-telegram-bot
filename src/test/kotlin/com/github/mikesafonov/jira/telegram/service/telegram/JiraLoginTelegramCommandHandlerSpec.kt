package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraLoginTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

/**
 * @author Mike Safonov
 */

class JiraLoginTelegramCommandHandlerSpec : BehaviorSpec({
    val telegramClient = mockk<TelegramClient>()

    Given("'/jira_login' telegram command handler") {
        val handler = JiraLoginTelegramCommandHandler(telegramClient)
        When("incoming message contain wrong command") {
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText( any()) } returns false
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and wrong state") {
            val command: TelegramCommand = mockk {
                every { text } returns "/jira_login"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isInState(State.INIT) } returns false
                every { isMatchText( "/jira_login") } returns true
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command") {
            val command: TelegramCommand = mockk {
                every { text } returns "/jira_login"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText( "/jira_login") } returns true
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("Chat found") {
            val randomId = Arb.long().next()
            val jiraLogin = Arb.string().next()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomId
                every { chat } returns mockk {
                    every { jiraId } returns jiraLogin
                    every { state } returns State.INIT
                }
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("Should return jira login message") {

                handler.handle(message) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        randomId,
                        "Your jira login: $jiraLogin"
                    )
                }
            }
        }
    }
})
