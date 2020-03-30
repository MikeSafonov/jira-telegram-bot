package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.NoChatTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

/**
 * @author Mike Safonov
 */
class NoChatTelegramCommandHandlerSpec : BehaviorSpec({
    val telegramClient = mockk<TelegramClient>()

    Given("no chat telegram handler") {
        val handler = NoChatTelegramCommandHandler(telegramClient)

        When("chat exist") {
            val commandWithChat = mockk<TelegramCommand> {
                every { chat } returns mockk {}
            }
            Then("isHandle returns false") {
                handler.isHandle(commandWithChat) shouldBe false
            }
        }

        When("no chat") {
            val telegramChatId = 1L
            val commandWithoutChat = mockk<TelegramCommand> {
                every { chat } returns null
                every { chatId } returns telegramChatId
            }
            every { telegramClient.sendTextMessage(any(), any()) } just Runs
            Then("isHandle returns true") {
                handler.isHandle(commandWithoutChat) shouldBe true
            }

            Then("return expected message"){
                handler.handle(commandWithoutChat) shouldBe State.INIT
                verify {
                    telegramClient.sendTextMessage(telegramChatId,
                        "You not registered at this bot yet. Please contact your system administrator for registration.")
                }
            }
        }
    }
})
