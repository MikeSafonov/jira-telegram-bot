package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.AddUserTelegramRequestHandler
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
class AddUserTelegramRequestHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val handler = AddUserTelegramRequestHandler(botProperties, chatRepository)

    Given("'/add_user' telegram command handler") {
        When("incoming message contain wrong command and user not admin") {
            Then("isHandle returns false") {
                every { botProperties.adminId } returns null
                handler.isHandle(mockk {
                    every { text } returns Gen.string().random().first()
                }) shouldBe false
            }
        }

        When("incoming message contain right command and user not admin") {
            Then("isHandle returns false") {
                every { botProperties.adminId } returns null
                handler.isHandle(
                    mockk {
                        every { text } returns "/add_user"
                    }) shouldBe false
            }
        }

        When("incoming message contain wrong command and user admin") {
            Then("isHandle returns false") {
                val admin = Gen.long().random().first()
                every { botProperties.adminId } returns admin
                handler.isHandle(mockk {
                    every { text } returns Gen.string().random().first()
                    every { chatId } returns admin
                }) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            Then("isHandle returns true") {
                val admin = Gen.long().random().first()
                every { botProperties.adminId } returns admin
                handler.isHandle(
                    mockk {
                        every { text } returns "/add_user"
                        every { chatId } returns admin
                    }) shouldBe true
            }
        }

        When("incoming message not contain expected arguments") {
            Then("returns message with text about command correct syntax") {
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command syntax\n Should be: /add_user <jiraLogin> <telegramId>"
                }
                handler.handle(
                    mockk {
                        every { text } returns "/add_user"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage

                handler.handle(
                    mockk {
                        every { text } returns "/add_user myLogin"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("jiraLogin in incoming message exists in database") {
            Then("returns message with text about jira login exists in database") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = Gen.string().random().first().replace(" ", "_")
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Jira login $jiraLogin already exist"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns Chat(
                    Gen.int().random().first(),
                    jiraLogin, Gen.long().random().first()
                )
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message is not a number") {
            Then("returns message with text about telegramId must be non negative number") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = Gen.string().random().first().replace(" ", "_")
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command args: telegramId must be a positive number"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns null
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message is negative") {
            Then("returns message with text about telegramId must not be negative") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = -1000
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command args: telegramId must be a positive number"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns null
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("telegramId in incoming message exists in database") {
            Then("returns message with text about telegramId exists in database") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = Gen.long().random().first()
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Telegram id $telegramId already exist"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns null
                every { chatRepository.findByTelegramId(telegramId) } returns Chat(
                    Gen.int().random().first(),
                    Gen.string().random().first(), telegramId
                )
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("exception fired when saving new chat to database") {
            Then("returns message with text about unexpected exception") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = Gen.long().random().first()
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Unexpected error"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns null
                every { chatRepository.findByTelegramId(telegramId) } returns null
                val chat = Chat(null, jiraLogin, telegramId)
                every { chatRepository.save(chat) } throws RuntimeException("")
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("successfully saved in database") {
            Then("returns message with text about new chat") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val telegramId = Gen.long().random().first()
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Jira user $jiraLogin with telegram id $telegramId was added successfully"
                }
                every { chatRepository.findByJiraId(jiraLogin) } returns null
                every { chatRepository.findByTelegramId(telegramId) } returns null
                val chat = Chat(null, jiraLogin, telegramId)
                every { chatRepository.save(chat) } returns chat
                handler.handle(
                    mockk {
                        every { text } returns "/add_user $jiraLogin $telegramId"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
                verify {
                    chatRepository.save(chat)
                }
            }
        }
    }
})