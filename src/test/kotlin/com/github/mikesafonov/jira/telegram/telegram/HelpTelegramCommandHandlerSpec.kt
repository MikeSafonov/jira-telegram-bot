package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
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

    Given("'/help' telegram command handler") {
        val handler = HelpTelegramCommandHandler(botProperties, TelegramMessageBuilder())

        When("incoming message contain wrong command") {
            val command : TelegramCommand = mockk {
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
            val command : TelegramCommand = mockk {
                every { text } returns  "/help"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(
                    command) shouldBe false
            }
        }

        When("incoming message contain right command ") {
            val command : TelegramCommand = mockk {
                every { text } returns  "/help"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns true") {
                handler.isHandle(
                    command) shouldBe true
            }
        }

        When("Message processing and user not admin") {
            every { botProperties.adminId } returns null
            val randomChatId = Gen.long().random().first()
            val message: TelegramCommand = mockk {
                every { chatId } returns randomChatId
            }

            val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message""".trimMargin()
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
            val message : TelegramCommand = mockk {
                every { chatId } returns admin
            }

            val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message
/users_list - prints list of users
/add_user <jiraLogin> <telegramId> -  add new user to bot
/remove_user <jiraLogin> - remove user from bot
                    """.trimMargin()
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