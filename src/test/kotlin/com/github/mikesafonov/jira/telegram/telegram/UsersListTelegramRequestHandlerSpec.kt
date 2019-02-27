package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UsersListTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class UsersListTelegramRequestHandlerSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val handler = UsersListTelegramRequestHandler(botProperties, chatRepository)

    Given("'/users_list' telegram command handler") {
        When("incoming message contain wrong command and user not admin") {
            Then("isHandle returns false") {
                every { botProperties.adminId } returns null
                handler.isHandle(mockk {
                    every { text } returns Gen.string().random().first()
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

        When("incoming message contain right command and user not admin") {
            Then("isHandle returns false") {
                every { botProperties.adminId } returns null
                handler.isHandle(
                    mockk {
                        every { text } returns "/users_list"
                    }) shouldBe false
            }
        }

        When("incoming message contain right command and user admin") {
            Then("isHandle returns true") {
                val admin = Gen.long().random().first()
                every { botProperties.adminId } returns admin
                handler.isHandle(
                    mockk {
                        every { text } returns "/users_list"
                        every { chatId } returns admin
                    }) shouldBe true
            }
        }

        When("Message processing") {
            Then("Should return expected users list") {
                val id = Gen.long().random().first()
                val allChats = listOf(
                    Chat(Gen.int().random().first(), Gen.string().random().first(), Gen.long().random().first()),
                    Chat(Gen.int().random().first(), Gen.string().random().first(), Gen.long().random().first()),
                    Chat(Gen.int().random().first(), Gen.string().random().first(), Gen.long().random().first())
                )
                every { chatRepository.findAll() } returns allChats
                val messageBuilder = StringBuilder("Jira users: \n")
                allChats.forEach {
                    messageBuilder.append("- ${it.jiraId}")
                }

                val expectedMessage = SendMessage().apply {
                    chatId = id.toString()
                    text = messageBuilder.toString()
                }

                handler.handle(mockk {
                    every { chatId } returns id
                }) shouldBe expectedMessage
            }
        }
    }
})
