package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.HelpTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */

class HelpTelegramCommandHandlerSpec : BehaviorSpec({

    val botProperties = mockk<BotProperties>()
    val jiraOAuthProperties = mockk<JiraOAuthProperties>()

    Given("'/help' telegram command handler") {
        val handler = HelpTelegramCommandHandler(botProperties, jiraOAuthProperties, TelegramMessageBuilder())

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

        When("incoming message contain right command and wrong state ") {
            val command: TelegramCommand = mockk {
                every { text } returns "/help"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(
                    command
                ) shouldBe false
            }
        }

        When("incoming message contain right command ") {
            val command: TelegramCommand = mockk {
                every { text } returns "/help"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(
                    command
                ) shouldBe true
            }
        }

        When("Message processing and user not admin") {
            every { botProperties.adminId } returns null
            every { jiraOAuthProperties.isNotEmpty } returns false
            val randomChatId = Gen.long().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomChatId
            }

            val helpMessage = HelpTelegramCommandHandler.DEFAULT_HELP_MESSAGE
            val id = message.chatId
            Then("Should return expected help message") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }, State.INIT)

                handler.handle(message) shouldBe expectedMessage
            }
        }

        When("Message processing and user not admin and jira allowed") {
            every { botProperties.adminId } returns null
            every { jiraOAuthProperties.isNotEmpty } returns true
            val randomChatId = Gen.long().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomChatId
            }

            val helpMessage = HelpTelegramCommandHandler.DEFAULT_HELP_MESSAGE + """
                /auth - start jira OAuth
            """.trimIndent()
            val id = message.chatId
            Then("Should return expected help message") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }, State.INIT)

                handler.handle(message) shouldBe expectedMessage
            }
        }

        When("Message processing and user admin") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            every { jiraOAuthProperties.isNotEmpty } returns false
            val message: TelegramCommand = mockk {
                every { chatId } returns admin
            }

            val helpMessage = HelpTelegramCommandHandler.ADMIN_HELP_MESSAGE
            val id = message.chatId
            Then("Should return expected help message") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }, State.INIT)

                handler.handle(message) shouldBe expectedMessage
            }
        }

        When("Message processing and user admin and jira allowed") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            every { jiraOAuthProperties.isNotEmpty } returns true
            val message: TelegramCommand = mockk {
                every { chatId } returns admin
            }

            val helpMessage = HelpTelegramCommandHandler.ADMIN_HELP_MESSAGE + """
                /auth - start jira OAuth
            """.trimIndent()
            val id = message.chatId
            Then("Should return expected help message") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }, State.INIT)

                handler.handle(message) shouldBe expectedMessage
            }
        }
    }
})