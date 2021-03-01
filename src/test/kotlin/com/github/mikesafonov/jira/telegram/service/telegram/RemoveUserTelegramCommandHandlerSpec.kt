package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.RemoveUserTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

/**
 * @author Mike Safonov
 */
class RemoveUserTelegramCommandHandlerSpec : BehaviorSpec({
    val chatService = mockk<ChatService>()
    val botProperties = mockk<BotProperties>()
    val telegramClient = mockk<TelegramClient>()

    Given("'/remove_user' telegram command handler") {
        val handler = RemoveUserTelegramCommandHandler(botProperties, chatService, telegramClient)
        When("incoming message contain wrong command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
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
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
                every { hasText } returns true
                every { chatId } returns admin
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

        When("incoming message contain right command and user admin and wrong state") {
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user"
                every { chatId } returns admin
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isInState(State.INIT) } returns false
                every { isStartsWithText("/remove_user") } returns true
            }

            Then("isHandle returns false") {
                handler.isHandle(
                    command
                ) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isStartsWithText("/remove_user") } returns true
            }

            Then("isHandle returns true") {
                handler.isHandle(
                    command
                ) shouldBe true
            }
        }

        When("incoming message not contain expected arguments") {
            val messageChatId = Arb.long().next()
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
            val jiraLogin = Arb.string().next().replace(" ", "_")
            val messageChatId = Arb.long().next()
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user $jiraLogin"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            every { chatService.deleteByJiraId(jiraLogin) } throws RuntimeException("")
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
            val jiraLogin = Arb.string().next().replace(" ", "_")
            val messageChatId = Arb.long().next()
            val command: TelegramCommand = mockk {
                every { text } returns "/remove_user $jiraLogin"
                every { chatId } returns messageChatId
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            every { chatService.deleteByJiraId(jiraLogin) } returns Unit
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("returns message with text about new chat") {
                handler.handle(command) shouldBe State.INIT

                verify {
                    chatService.deleteByJiraId(jiraLogin)
                    telegramClient.sendTextMessage(
                        messageChatId,
                        "User $jiraLogin was removed successfully"
                    )
                }
            }
        }
    }
})
