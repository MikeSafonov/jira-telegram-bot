package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramHandlersHolder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class TelegramHandlersHolderSpec : BehaviorSpec({

    Given("telegram handlers holder without handlers") {
        val holder = TelegramHandlersHolder(emptyList())
        When("any command incoming") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return null handler") {
                holder.findHandler(command) shouldBe null
            }
        }
    }

    Given("telegram handlers holder with always success handler") {
        val handler = mockk<TelegramCommandHandler>{
            every { isHandle(any()) } returns true
        }
        val holder = TelegramHandlersHolder(listOf(
            handler
        ))
        When("command incoming") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe handler
            }
        }
    }

    Given("telegram handlers holder with always false handler") {
        val handler = mockk<TelegramCommandHandler>{
            every { isHandle(any()) } returns false
        }
        val holder = TelegramHandlersHolder(listOf(
            handler
        ))
        When("command incoming") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns Gen.long().random().first()
                every { hasText } returns true
            }

            Then("return expected handler") {
                holder.findHandler(command) shouldBe null
            }
        }
    }
})
