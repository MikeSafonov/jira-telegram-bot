package com.github.mikesafonov.jira.telegram.telegram

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommand
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramCommandExecutor
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramHandlersHolder
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramMessageBuilder
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

/**
 * @author Mike Safonov
 */
class TelegramCommandExecutorSpec : BehaviorSpec({

    val chatRepository = mockk<ChatRepository>()
    val holder = mockk<TelegramHandlersHolder>()
    val telegramMessageBuilder = TelegramMessageBuilder()

    Given("telegram command executor") {
        val executor = TelegramCommandExecutor(holder, chatRepository, telegramMessageBuilder)

        When("no chat") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns null
            }


            Then("return unknown chat response") {

                val unknownChatResponse = SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text =
                        "You not registered at this bot yet. Please contact your system administrator for registration."
                }

                executor.execute(command) {
                    it shouldBe unknownChatResponse
                }

                verify {
                    chatRepository wasNot Called
                }

            }
        }

        When("no handler") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            every { holder.findHandler(command) } returns null


            Then("return unknown command response") {

                val unknownChatResponse = SendMessage().apply {
                    chatId = telegramChatId.toString()
                    text =
                        "Unknown command. Please use /help to see allowed commands"
                }

                executor.execute(command) {
                    it shouldBe unknownChatResponse
                }

                verify {
                    chatRepository wasNot Called
                }

            }
        }

        When("handler exist and no need to change chat state") {
            val telegramChatId = 1L
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
            }

            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(command) } returns true
                every { handle(command) } returns mockk {
                    every { nextState } returns State.INIT
                    every { method } returns mockk {}
                }
            }

            every { holder.findHandler(command) } returns commandHandler

            Then("return command response and not change chat state") {

                executor.execute(command) {
                }

                verify {
                    chatRepository wasNot Called
                }

            }
        }

        When("handler exist and  need to change chat state") {
            val telegramChatId = 1L
            val chatEntity = Chat(1, "", telegramChatId, State.INIT)
            val expectedChat = Chat(1, "", telegramChatId, State.WAIT_APPROVE)
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { chatId } returns telegramChatId
                every { hasText } returns true
                every { chat } returns chatEntity
            }

            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(command) } returns true
                every { handle(command) } returns mockk {
                    every { nextState } returns State.WAIT_APPROVE
                    every { method } returns mockk {}
                }
            }

            every { holder.findHandler(command) } returns commandHandler
            every { chatRepository.save(expectedChat) } returns expectedChat

            Then("return command response and change chat state") {


                executor.execute(command) {
                }

                verify {
                    chatRepository.save(expectedChat)
                }

            }
        }
    }

})