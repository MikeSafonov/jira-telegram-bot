package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.AddUserTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class AddUserTelegramCommandHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()

    Given("'/add_user' telegram command handler") {
        val handler = AddUserTelegramCommandHandler(botProperties, chatRepository, TelegramMessageBuilder())

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

            Then("returns message with text about command correct syntax") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command syntax\n Should be: /add_user <jiraLogin> <telegramId>"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage

                handler.handle(
                    mockk {
                        every { text } returns "/add_user myLogin"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("jiraLogin in incoming message exists in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.string().random().first().replace(" ", "_")
            val messageChatId = Gen.long().random().first()
            every { chatRepository.findByJiraId(jiraLogin) } returns Chat(
                Gen.int().random().first(),
                jiraLogin, Gen.long().random().first(), State.INIT
            )
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            Then("returns message with text about jira login exists in database") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Jira login $jiraLogin already exist"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message is not a number") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.string().random().first().replace(" ", "_")
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { chatRepository.findByJiraId(jiraLogin) } returns null


            Then("returns message with text about telegramId must be non negative number") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command args: telegramId must be a positive number"
                }, State.INIT)
                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message is negative") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = -1000
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            every { chatRepository.findByJiraId(jiraLogin) } returns null

            Then("returns message with text about telegramId must not be negative") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command args: telegramId must be a positive number"
                }, State.INIT)
                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message exists in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            every { chatRepository.findByTelegramId(telegramId) } returns Chat(
                Gen.int().random().first(),
                Gen.string().random().first(), telegramId, State.INIT
            )
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            Then("returns message with text about telegramId exists in database") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Telegram id $telegramId already exist"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("exception fired when saving new chat to database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            every { chatRepository.findByTelegramId(telegramId) } returns null
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.save(chat) } throws RuntimeException("")
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }

            Then("returns message with text about unexpected exception") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Unexpected error"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("successfully saved in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val telegramId = Gen.long().random().first()
            val messageChatId = Gen.long().random().first()
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            every { chatRepository.findByTelegramId(telegramId) } returns null
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.save(chat) } returns chat
            val command: TelegramCommand = mockk {
                every { chatId } returns messageChatId
                every { text } returns "/add_user $jiraLogin $telegramId"
            }
            Then("returns message with text about new chat") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Jira user $jiraLogin with telegram id $telegramId was added successfully"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage
                verify {
                    chatRepository.save(chat)
                }
            }
        }
    }
})