package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.RemoveUserTelegramCommandHandler
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
class RemoveUserTelegramCommandHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()

    Given("'/remove_user' telegram command handler") {
        val handler = RemoveUserTelegramCommandHandler(botProperties, chatRepository, TelegramMessageBuilder())
        When("incoming message contain wrong command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user"
                every { chat } returns mockk {
                    every { state } returns State.INIT
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
                every { hasText } returns true
                every { chatId } returns admin
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user admin and wrong state") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user"
                every { chatId } returns admin
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
                every { text } returns "/remove_user"
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
                every { text } returns "/remove_user"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("returns message with text about command correct syntax") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Wrong command syntax\n Should be: /remove_user <jiraLogin>"
                }, State.INIT)

                handler.handle(
                    command
                ) shouldBe expectedMessage
            }
        }

        When("exception fired when deleting chat from database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user $jiraLogin"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            every { chatRepository.deleteByJiraId(jiraLogin) } throws RuntimeException("")

            Then("returns message with text about unexpected exception") {

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "Unexpected error"
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }

        When("successfully saved in database") {
            val jiraLogin = Gen.string().random().first().replace(" ", "_")
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user $jiraLogin"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            every { chatRepository.deleteByJiraId(jiraLogin) } returns Unit

            Then("returns message with text about new chat") {
                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = messageChatId.toString()
                    text = "User $jiraLogin was removed successfully"
                }, State.INIT)
                handler.handle(command) shouldBe expectedMessage
                verify {
                    chatRepository.deleteByJiraId(jiraLogin)
                }
            }
        }
    }
})