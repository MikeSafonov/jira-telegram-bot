package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.AddChatException
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.AddUserTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.*

/**
 * @author Mike Safonov
 */
class AddUserTelegramCommandHandlerSpec : BehaviorSpec({
    val chatService = mockk<ChatService>()
    val botProperties = mockk<BotProperties>()
    val telegramClient = mockk<TelegramClient>()

    Given("'/add_user' telegram command handler") {
        val handler = AddUserTelegramCommandHandler(chatService, botProperties, telegramClient)

        When("incoming message contain wrong command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { message } returns mockk {
                    every { text } returns Gen.string().random().first()
                }
            }

            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { message } returns mockk {
                    every { text } returns "/add_user"
                }
            }

            Then("isHandle returns false") {
                handler.isHandle(
                    command
                ) shouldBe false
            }
        }

        When("incoming message contain wrong command and user admin") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isStartsWithText(any()) } returns false
            }

            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user admin, but not INIT state") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/add_user"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isInState(State.INIT) } returns false
            }

            Then("isHandle returns false") {
                handler.isHandle(
                    command
                ) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/add_user"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isStartsWithText("/add_user") } returns true
            }

            Then("isHandle returns true") {
                handler.isHandle(
                    command
                ) shouldBe true
            }
        }


        When("incoming message not contain expected arguments") {
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user"
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs

            Then("returns message with text about command incorrect syntax") {
                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Wrong command syntax\n Should be: /add_user <jiraLogin> <telegramId>"
                    )
                }

                handler.handle(
                    mockk {
                        every { text } returns "/add_user myLogin"
                        every { chatId } returns messageChatId
                    }) shouldBe State.INIT
            }
        }

        When("telegramId in incoming message is not a number") {
            val jiraLogin = "jira_login"
            val telegramId = "asY4"
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs

            Then("returns message with text about telegramId must be non negative number") {

                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Wrong command args: telegramId must be a positive number"
                    )
                }
            }
        }

        When("chat exists in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()

            every { chatService.addNewChat(jiraLogin, telegramId) } throws AddChatException("Jira login $jiraLogin already exist")

            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about jira login exists in database") {

                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Jira login $jiraLogin already exist"
                    )
                }
            }
        }


        When("exception fired when saving new chat to database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()

            every { chatService.addNewChat(jiraLogin, telegramId) } throws Exception("Error")

            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs

            Then("returns message with text about unexpected exception") {
                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Unexpected error"
                    )
                }
            }
        }

        When("successfully saved in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()

            every { chatService.addNewChat(jiraLogin, telegramId) } just Runs

            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about new chat") {
                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Jira user $jiraLogin with telegram id $telegramId was added successfully"
                    )
                }
            }
        }
    }
})
