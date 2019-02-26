package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.config.BotProperties
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.AddUserTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class AddUserTelegramRequestHandlerSpec : StringSpec({
    val chatRepository = mockk<ChatRepository>()
    val botProperties = mockk<BotProperties>()
    val handler = AddUserTelegramRequestHandler(botProperties, chatRepository)

    "isHandle should return expected value"{
        every { botProperties.adminId } returns null
        handler.isHandle(
            mockk {
                every { text } returns "/add_user"
            }) shouldBe false
        handler.isHandle(mockk {
            every { text } returns Gen.string().random().first()
        }) shouldBe false

        val admin = Gen.long().random().first()
        every { botProperties.adminId } returns admin
        handler.isHandle(
            mockk {
                every { text } returns "/add_user"
                every { chat } returns mockk {
                    every { id } returns admin
                }
            }) shouldBe true
        handler.isHandle(mockk {
            every { text } returns Gen.string().random().first()
            every { chat } returns mockk {
                every { id } returns admin
            }
        }) shouldBe false
    }

})