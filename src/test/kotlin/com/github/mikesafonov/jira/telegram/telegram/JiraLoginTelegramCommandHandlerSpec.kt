package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraLoginTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */

class JiraLoginTelegramCommandHandlerSpec : BehaviorSpec({

    Given("'/jira_login' telegram command handler") {
        val handler = JiraLoginTelegramCommandHandler(TelegramMessageBuilder())
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
                every { text } returns "/jira_login"
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
                every { text } returns "/jira_login"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("Chat found") {
            val randomId = Gen.long().random().first()
            val jiraLogin = Gen.string().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomId
                every { chat } returns mockk {
                    every { jiraId } returns jiraLogin
                    every { state } returns State.INIT
                }
            }

            Then("Should return jira login message") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = randomId.toString()
                    text = "Your jira login: $jiraLogin"
                }, State.INIT)

                handler.handle(message) shouldBe expectedMessage
            }
        }
    }
})