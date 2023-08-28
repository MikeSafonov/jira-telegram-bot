package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.UnknownCommandTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class TelegramHandlersHolderSpec : BehaviorSpec({

    Given("telegram handlers holder with default handlers") {
        val unknownHandler = UnknownCommandTelegramCommandHandler(mockk())
        val holder = TelegramHandlersHolder(
            listOf(
                unknownHandler
            )
        )

        When("command with chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk()
                every { text } returns Arb.string().next()
                every { chatId } returns Arb.long().next()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe unknownHandler
            }
        }
    }

    Given("telegram handlers holder with handlers") {
        val unknownHandler = UnknownCommandTelegramCommandHandler(mockk())
        val alwaysSuccessHandler = mockk<TelegramCommandHandler>{
            every { isHandle(any()) } returns true
        }
        val holder = TelegramHandlersHolder(
            listOf(
                unknownHandler, alwaysSuccessHandler
            )
        )

        When("command with chat incoming") {
            val command: TelegramCommand = mockk {
                every { chat } returns mockk()
                every { text } returns Arb.string().next()
                every { chatId } returns Arb.long().next()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe alwaysSuccessHandler
            }
        }
    }
})
