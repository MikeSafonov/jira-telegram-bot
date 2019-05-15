package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandResponse
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UsersListTelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class UsersListTelegramCommandHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val handler = UsersListTelegramCommandHandler(botProperties, chatRepository, TelegramMessageBuilder())

    Given("'/users_list' telegram command handler") {
        When("incoming message contain wrong command and user not admin") {
            every { botProperties.adminId } returns null
            val command: TelegramCommand = mockk {
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
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/users_list"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
            }

            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            val admin = Gen.long().random().first()
            every { botProperties.adminId } returns admin
            val command: TelegramCommand = mockk {
                every { text } returns "/users_list"
                every { chatId } returns admin
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("Message processing") {
            val id = Gen.long().random().first()
            val allChats = listOf(
                Chat(
                    Gen.int().random().first(),
                    Gen.string().random().first(),
                    Gen.long().random().first(),
                    State.INIT
                ),
                Chat(
                    Gen.int().random().first(),
                    Gen.string().random().first(),
                    Gen.long().random().first(),
                    State.INIT
                ),
                Chat(Gen.int().random().first(), Gen.string().random().first(), Gen.long().random().first(), State.INIT)
            )
            every { chatRepository.findAll() } returns allChats
            val command: TelegramCommand = mockk {
                every { chatId } returns id
            }

            Then("Should return expected users list") {
                val messageBuilder = StringBuilder("Jira users: \n")
                allChats.forEach {
                    messageBuilder.append("- ${it.jiraId}\n")
                }

                val expectedMessage = TelegramCommandResponse(SendMessage().apply {
                    chatId = id.toString()
                    text = messageBuilder.toString()
                }, State.INIT)

                handler.handle(command) shouldBe expectedMessage
            }
        }
    }
})
