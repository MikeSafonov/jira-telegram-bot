package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.HelpTelegramRequestHandler
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

class HelpTelegramRequestHandlerSpec : StringSpec({

    val handler = HelpTelegramRequestHandler()

    "isHandle should return expected value"{
        handler.isHandle("/help") shouldBe true
        handler.isHandle(Gen.string().random().first()) shouldBe false
    }

    "Should return expected help message"{
        val randomChatId = Gen.long().random().first()
        val message = mockk<Message> {
            every { chatId } returns randomChatId
        }

        val helpMessage = """This is jira-telegram-bot. Supported commands:
/me - prints telegram chat id
/jira_login - prints attached jira login to this telegram chat id
/help - prints help message""".trimMargin()
        val id = message.chatId
        val expectedMessage = SendMessage().apply {
            chatId = id.toString()
            text = helpMessage
        }

        handler.handle(message) shouldBe expectedMessage
    }
})