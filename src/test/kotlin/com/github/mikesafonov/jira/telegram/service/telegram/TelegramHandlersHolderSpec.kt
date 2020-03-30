package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.NoChatTelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownCommandTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.long
import io.kotest.properties.string
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class TelegramHandlersHolderSpec : BehaviorSpec({

    Given("telegram handlers holder with default handlers") {
        val noChatHandler = NoChatTelegramCommandHandler(mockk())
        val unknownHandler = UnknownCommandTelegramCommandHandler(mockk())
        val holder = TelegramHandlersHolder(
            listOf(
                noChatHandler, unknownHandler
            )
        )
        When("command without chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns null
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe noChatHandler
            }
        }

        When("command with chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk()
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe unknownHandler
            }
        }
    }

    Given("telegram handlers holder with handlers") {
        val noChatHandler = NoChatTelegramCommandHandler(mockk())
        val unknownHandler = UnknownCommandTelegramCommandHandler(mockk())
        val alwaysSuccessHandler = mockk<TelegramCommandHandler>{
            every { isHandle(any()) } returns true
        }
        val holder = TelegramHandlersHolder(
            listOf(
                noChatHandler, unknownHandler, alwaysSuccessHandler
            )
        )
        When("command without chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns null
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe noChatHandler
            }
        }

        When("command with chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk()
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe alwaysSuccessHandler
            }
        }
    }
})
