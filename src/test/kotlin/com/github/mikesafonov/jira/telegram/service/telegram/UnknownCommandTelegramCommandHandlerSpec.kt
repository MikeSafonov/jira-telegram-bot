package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownCommandTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

/**
 * @author Mike Safonov
 */
class UnknownCommandTelegramCommandHandlerSpec : BehaviorSpec({
    val telegramClient = mockk<TelegramClient>()

    Given("handler for unknown telegram commands") {
        val handler = UnknownCommandTelegramCommandHandler(telegramClient)

        When("unknown command") {
            val telegramChatId = 1L

            val command = mockk<TelegramCommand> {
                every { chatId } returns telegramChatId
            }

            every { telegramClient.sendTextMessage(any(), any()) } just Runs

            Then("return expected message") {

                handler.isHandle(command) shouldBe false
                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        telegramChatId,
                        "Unknown command. Please use /help to see allowed commands"
                    )
                }
            }
        }


    }
})
