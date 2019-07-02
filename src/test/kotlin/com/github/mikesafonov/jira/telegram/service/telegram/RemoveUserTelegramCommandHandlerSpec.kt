package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.RemoveUserTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.*

/**
 * @author Mike Safonov
 */
class RemoveUserTelegramCommandHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val telegramClient = mockk<TelegramClient>()

    Given("'/remove_user' telegram command handler") {
        val handler = RemoveUserTelegramCommandHandler(botProperties, chatRepository, telegramClient)
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
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about command correct syntax") {

                handler.handle(
                    command
                ) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "Wrong command syntax\n Should be: /remove_user <jiraLogin>"
                    )
                }
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
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about unexpected exception") {
                handler.handle(command) shouldBe State.INIT

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
            val messageChatId = Gen.long().random().first()
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user $jiraLogin"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            every { chatRepository.deleteByJiraId(jiraLogin) } returns Unit
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about new chat") {
                 handler.handle(command) shouldBe State.INIT

                verify {
                    chatRepository.deleteByJiraId(jiraLogin)
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "User $jiraLogin was removed successfully"
                    )
                }
            }
        }
    }
})
