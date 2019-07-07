package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.config.BuildInfo
import com.github.mikesafonov.jira.telegram.config.JiraOAuthProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.HelpTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.*

/**
 * @author Mike Safonov
 */

class HelpTelegramCommandHandlerSpec : BehaviorSpec({

    val botProperties = mockk<BotProperties>()
    val jiraOAuthProperties = mockk<JiraOAuthProperties>()
    val telegramClient = mockk<TelegramClient>()
    val buildInfo = mockk<BuildInfo>()

    Given("'/help' telegram command handler") {
        val handler =
            HelpTelegramCommandHandler(botProperties, jiraOAuthProperties, buildInfo, telegramClient)

        When("incoming message contain wrong command") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText(any()) } returns false
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
                every { isInState(State.INIT) } returns false
                every { isStartsWithText("/help") } returns true
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
                every { isInState(State.INIT) } returns true
                every { isMatchText("/help") } returns true
            }
            Then("isHandle returns true") {
                handler.isHandle(
                    command
                ) shouldBe true
            }
        }

        When("Message processing and user not admin") {
            val currentVersion = "1.1.0"
            every { buildInfo.version } returns currentVersion
            every { botProperties.adminId } returns null
            every { jiraOAuthProperties.isNotEmpty } returns false
            val randomChatId = Gen.long().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomChatId
            }
            every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
            val helpMessage = """This is [jira-telegram-bot](https://github.com/MikeSafonov/jira-telegram-bot) version *$currentVersion*

${HelpTelegramCommandHandler.DEFAULT_HELP_MESSAGE}"""
            val id = message.chatId
            Then("Should return expected help message") {

                handler.handle(message) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(
                        id,
                        helpMessage
                    )
                }
            }
        }

        When("Message processing and user not admin and jira allowed") {
            val currentVersion = "1.1.0"
            every { buildInfo.version } returns currentVersion
            every { botProperties.adminId } returns null
            every { jiraOAuthProperties.isNotEmpty } returns true
            val randomChatId = Gen.long().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomChatId
            }
            every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
            val helpMessage = """This is [jira-telegram-bot](https://github.com/MikeSafonov/jira-telegram-bot) version *$currentVersion*

${HelpTelegramCommandHandler.DEFAULT_HELP_MESSAGE}

Jira commands:
/auth - start jira OAuth
            """.trimIndent()
            val id = message.chatId
            Then("Should return expected help message") {

                handler.handle(message) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(
                        id,
                        helpMessage
                    )
                }
            }
        }

        When("Message processing and user admin") {
            val admin = Gen.long().random().first()
            val currentVersion = "1.1.0"
            every { buildInfo.version } returns currentVersion
            every { botProperties.adminId } returns admin
            every { jiraOAuthProperties.isNotEmpty } returns false
            val message: TelegramCommand = mockk {
                every { chatId } returns admin
            }
            every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
            val helpMessage = """This is [jira-telegram-bot](https://github.com/MikeSafonov/jira-telegram-bot) version *$currentVersion*

${HelpTelegramCommandHandler.ADMIN_HELP_MESSAGE}"""
            val id = message.chatId
            Then("Should return expected help message") {

                handler.handle(message) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(
                        id,
                        helpMessage
                    )
                }
            }
        }

        When("Message processing and user admin and jira allowed") {
            val admin = Gen.long().random().first()
            val currentVersion = "1.1.0"
            every { buildInfo.version } returns currentVersion
            every { botProperties.adminId } returns admin
            every { jiraOAuthProperties.isNotEmpty } returns true
            val message: TelegramCommand = mockk {
                every { chatId } returns admin
            }
            every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
            val helpMessage = """This is [jira-telegram-bot](https://github.com/MikeSafonov/jira-telegram-bot) version *$currentVersion*

${HelpTelegramCommandHandler.ADMIN_HELP_MESSAGE}

Jira commands:
/auth - start jira OAuth
            """.trimIndent()
            val id = message.chatId
            Then("Should return expected help message") {

                handler.handle(message) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(
                        id,
                        helpMessage
                    )
                }
            }
        }
    }
})
