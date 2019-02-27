package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.HelpTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */

class HelpTelegramRequestHandlerSpec : BehaviorSpec({

    val botProperties = mockk<BotProperties>()
    val handler = HelpTelegramRequestHandler(botProperties)

    Given("'/help' telegram command handler") {
        When("incoming message contain wrong command") {
            Then("isHandle returns false") {
                handler.isHandle(mockk {
                    every { text } returns Gen.string().random().first()
                }) shouldBe false
            }
        }

        When("incoming message contain right command") {
            Then("isHandle returns true") {
                handler.isHandle(
                    mockk {
                        every { text } returns "/help"
                    }) shouldBe true
            }
        }

        When("Message processing and user not admin") {
            Then("Should return expected help message") {
                every { botProperties.adminId } returns null
                val randomChatId = Gen.long().random().first()
                val message = mockk<Message> {
                    every { chatId } returns randomChatId
                }

                val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message""".trimMargin()
                val id = message.chatId
                val expectedMessage = SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }

                handler.handle(message) shouldBe expectedMessage
            }
        }

        When("Message processing and user admin") {
            Then("Should return expected help message") {
                val admin = Gen.long().random().first()
                every { botProperties.adminId } returns admin
                val message = mockk<Message> {
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
                val expectedMessage = SendMessage().apply {
                    chatId = id.toString()
                    text = helpMessage
                }

                handler.handle(message) shouldBe expectedMessage
            }
        }
    }
})