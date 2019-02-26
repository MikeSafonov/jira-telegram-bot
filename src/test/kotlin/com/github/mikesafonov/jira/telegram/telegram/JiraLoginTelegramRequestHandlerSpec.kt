package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.JiraLoginTelegramRequestHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

/**
 * @author Mike Safonov
 */

class JiraLoginTelegramRequestHandlerSpec : StringSpec({

    val chatRepository = mockk<ChatRepository>()
    val handler = JiraLoginTelegramRequestHandler(chatRepository)

    "isHandle should return expected value"{
        handler.isHandle(
            mockk {
                every { text } returns "/jira_login"
            }) shouldBe true
        handler.isHandle(mockk {
            every { text } returns Gen.string().random().first()
        }) shouldBe false
    }

    "Should return not registered message"{
        val randomId = Gen.long().random().first()
        val expectedMessage = SendMessage().apply {
            chatId = randomId.toString()
            text = "You not registered at this bot yet. Please contact your system administrator for registration."
        }

        val message = mockk<Message> {
            every { chatId } returns randomId
        }
        every { chatRepository.findByTelegramId(randomId) } returns null

        handler.handle(message) shouldBe expectedMessage
    }

    "Should return jira login message"{
        val randomId = Gen.long().random().first()
        val jiraLogin = Gen.string().random().first()

        val expectedMessage = SendMessage().apply {
            chatId = randomId.toString()
            text = "Your jira login: $jiraLogin"
        }

        val message = mockk<Message> {
            every { chatId } returns randomId
        }
        every { chatRepository.findByTelegramId(randomId) } returns Chat(
            Gen.int().random().first(),
            jiraLogin,
            randomId
        )

        handler.handle(message) shouldBe expectedMessage
    }


})