package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.AddUserTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

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
    }
})