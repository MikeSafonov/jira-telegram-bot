package com.github.mikesafonov.jira.telegram.service.telegram

import io.kotlintest.properties.Gen
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

class TelegramClientSpec : BehaviorSpec({

    val absSender = mockk<DefaultAbsSender>()
    val telegramMessageBuilder = mockk<TelegramMessageBuilder>()

    val client = TelegramClient(absSender, telegramMessageBuilder)

    Given("telegram client") {
        val id = Gen.long().random().first()
        val message = Gen.string().random().first()
        val idMessage = Gen.int().random().first()

        When("simple message") {
            val sendMessage = mockk<SendMessage>()

            every { telegramMessageBuilder.createMessage(id, message) } returns sendMessage
            every { absSender.execute(any<SendMessage>()) } returns mockk()
            Then("call abs sender") {
                client.sendTextMessage(id, message)
                verify {
                    absSender.execute(sendMessage)
                }
            }
        }

        When("markdown message") {
            val sendMessage = mockk<SendMessage>()

            every { telegramMessageBuilder.createMarkdownMessage(id, message) } returns sendMessage
            every { absSender.execute(any<SendMessage>()) } returns mockk()
            Then("call abs sender") {
                client.sendMarkdownMessage(id, message)
                verify {
                    absSender.execute(sendMessage)
                }
            }
        }

        When("delete message") {
            val replaceMessage = mockk<DeleteMessage>()

            every { telegramMessageBuilder.createDeleteMessage(id.toString(), idMessage) } returns replaceMessage
            every { absSender.execute(any<DeleteMessage>()) } returns mockk()
            Then("call abs sender") {
                client.sendDeleteMessage(id, idMessage)
                verify {
                    absSender.execute(replaceMessage)
                }
            }
        }
    }
})
