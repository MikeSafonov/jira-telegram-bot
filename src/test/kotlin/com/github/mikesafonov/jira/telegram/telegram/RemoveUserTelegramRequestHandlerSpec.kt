package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.RemoveUserTelegramRequestHandler
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
class RemoveUserTelegramRequestHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val handler = RemoveUserTelegramRequestHandler(botProperties, chatRepository)

    Given("'/remove_user' telegram command handler") {
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
                        every { text } returns "/remove_user"
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
                        every { text } returns "/remove_user"
                        every { chatId } returns admin
                    }) shouldBe true
            }
        }

        When("incoming message not contain expected arguments") {
            Then("returns message with text about command correct syntax") {
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command syntax: Should be: /remove_user <jiraLogin>"
                }
                handler.handle(
                    mockk {
                        every { text } returns "/remove_user"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("exception fired when deleting chat from database") {
            Then("returns message with text about unexpected exception") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Unexpected error"
                }
                every { chatRepository.deleteByJiraId(jiraLogin) } throws RuntimeException("")
                handler.handle(
                    mockk {
                        every { text } returns "/remove_user $jiraLogin"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
            }
        }

        When("successfully saved in database") {
            Then("returns message with text about new chat") {
                val jiraLogin = Gen.string().random().first().replace(" ", "_")
                val messageChatId = Gen.long().random().first()
                val expectedMessage = SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "User $jiraLogin was removed successfully"
                }
                every { chatRepository.deleteByJiraId(jiraLogin) } returns Unit
                handler.handle(
                    mockk {
                        every { text } returns "/remove_user $jiraLogin"
                        every { chatId } returns messageChatId
                    }) shouldBe expectedMessage
                verify {
                    chatRepository.deleteByJiraId(jiraLogin)
                }
            }
        }
    }
})