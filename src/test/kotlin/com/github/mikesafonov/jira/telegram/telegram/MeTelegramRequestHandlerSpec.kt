package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.MeTelegramRequestHandler
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

class MeTelegramRequestHandlerSpec : StringSpec({
    val handler = MeTelegramRequestHandler()

    "isHandle should return expected value"{
        handler.isHandle("/me") shouldBe true
        handler.isHandle(Gen.string().random().first()) shouldBe false
    }

    "Should return chat id in message"{
        val randomId = Gen.long().random().first()
        val expectedMessage = SendMessage().apply {
            chatId = randomId.toString()
            text = "Your chat id: $randomId"
        }

        val message = mockk<Message> {
            every { chatId } returns randomId
        }

        handler.handle(message) shouldBe expectedMessage
    }
})