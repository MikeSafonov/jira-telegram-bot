package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UsersListTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

/**
 * @author Mike Safonov
 */
class UsersListTelegramCommandHandlerSpec : BehaviorSpec({
    val chatService = mockk<ChatService>()
    val botProperties = mockk<BotProperties>()
    val telegramClient = mockk<TelegramClient>()

    Given("'/users_list' telegram command handler") {

        val handler = UsersListTelegramCommandHandler(botProperties, chatService, telegramClient)
        When("incoming message contain wrong command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain wrong command and user admin") {
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
                every { chatId } returns admin
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

        When("incoming message contain right command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
                every { text } returns "/users_list"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user admin and wrong state") {
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/users_list"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isInState(State.INIT) } returns false
                every { isMatchText("/users_list") } returns true
            }

            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            val admin = Arb.long().next()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/users_list"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText("/users_list") } returns true
            }

            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("Message processing") {
            val id = Arb.long().next()
            val allChats = listOf(
                Chat(
                    Arb.int().next(),
                    Arb.string().next(),
                    Arb.long().next(),
                    State.INIT
                ),
                Chat(
                    Arb.int().next(),
                    Arb.string().next(),
                    Arb.long().next(),
                    State.INIT
                ),
                Chat(Arb.int().next(), Arb.string().next(), Arb.long().next(), State.INIT)
            )
            every { chatService.getAll() } returns allChats
            val command: TelegramCommand = mockk {
                every { chatId } returns id
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("Should return expected users list") {
                val messageBuilder = StringBuilder("Jira users: \n")
                allChats.forEach {
                    messageBuilder.append("- ${it.jiraId}\n")
                }

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        id,
                        messageBuilder.toString()
                    )
                }
            }
        }
    }
})
