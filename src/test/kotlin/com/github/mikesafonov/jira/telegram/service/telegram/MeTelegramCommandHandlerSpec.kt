package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.MeTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.*

/**
 * @author Mike Safonov
 */

class MeTelegramCommandHandlerSpec : BehaviorSpec({

    val telegramClient = mockk<TelegramClient>()

    Given("'/me' telegram command handler") {
        val handler = MeTelegramCommandHandler(telegramClient)
        When("incoming message contain wrong command") {
            val command: TelegramCommand = mockk {
                every { text } returns Arb.string().next()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isAnonymous() } returns false
                every { isInState(State.INIT) } returns true
                every { isMatchText(any()) } returns false
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and wrong state") {
            val command: TelegramCommand = mockk {
                every { text } returns "/me"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isAnonymous() } returns false
                every { isInState(State.INIT) } returns false
                every { isMatchText("/me") } returns true
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and anonymous") {
            val command: TelegramCommand = mockk {
                every { text } returns "/me"
                every { hasText } returns true
                every { chat } returns null
                every { isAnonymous() } returns true
                every { isInState(State.INIT) } returns false
                every { isMatchText("/me") } returns true
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe true
            }
        }

        When("incoming message contain right command") {
            val command: TelegramCommand = mockk {
                every { text } returns "/me"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isAnonymous() } returns false
                every { isInState(State.INIT) } returns true
                every { isMatchText("/me") } returns true
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }
        every { telegramClient.sendTextMessage(any(), any()) } just Runs
        When("Message processing") {
            val randomId = Arb.long().next()
            val command: TelegramCommand = mockk {
                every { chatId } returns randomId
            }
            Then("Should return users chat id") {
                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(
                        randomId,
                        "Your chat id: $randomId"
                    )
                }
            }
        }
    }
})
