package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.AuthorizationService
import com.github.mikesafonov.jira.telegram.service.ChatService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.telegram.telegrambots.meta.api.objects.Update

/**
 * @author Mike Safonov
 */
class TelegramUpdateManagerSpec : BehaviorSpec({

    val chatService = mockk<ChatService>()
    val holder = mockk<TelegramHandlersHolder>()
    val authorizationService = mockk<AuthorizationService>()

    Given("telegram update manager") {
        val manager = TelegramUpdateManager(chatService, authorizationService, holder)

        When("handler exist and no need to change chat state") {
            val telegramChatId = 1L
            val update: Update = mockk {
                every { hasMessage() } returns true
                every { message } returns mockk {
                    every { hasText() } returns true
                    every { chatId } returns telegramChatId
                }
            }

            every { chatService.findByTelegramId(telegramChatId) } returns mockk {
                every { state } returns State.INIT
            }
            every { authorizationService.get(any()) } returns null
            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(ofType(TelegramCommand::class)) } returns true
                every { handle(ofType(TelegramCommand::class)) } returns State.INIT
            }

            every { holder.findHandler(ofType(TelegramCommand::class)) } returns commandHandler

            Then("return command response and not change chat state") {

                manager.onUpdate(update)


                verify {
                    chatService.save(ofType(Chat::class)) wasNot Called
                }

            }
        }

        When("handler exist and chat is null") {
            val telegramChatId = 1L
            val update: Update = mockk {
                every { hasMessage() } returns true
                every { message } returns mockk {
                    every { hasText() } returns true
                    every { chatId } returns telegramChatId
                }
            }

            every { chatService.findByTelegramId(telegramChatId) } returns null
            every { authorizationService.get(any()) } returns null
            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(ofType(TelegramCommand::class)) } returns true
                every { handle(ofType(TelegramCommand::class)) } returns State.INIT
            }

            every { holder.findHandler(ofType(TelegramCommand::class)) } returns commandHandler
            every { chatService.addChatInState(any(), any(), any()) } just Runs

            Then("return command response and not change chat state") {

                manager.onUpdate(update)


                verify {
                    chatService.addChatInState(telegramChatId, null, State.INIT)
                }

            }
        }

        When("handler exist and need to change chat state") {
            val telegramChatId = 1L
            val update: Update = mockk {
                every { hasMessage() } returns true
                every { message } returns mockk {
                    every { hasText() } returns true
                    every { chatId } returns telegramChatId
                }
            }

            val oldChat = Chat(1, "", telegramChatId, State.INIT)
            val expectedChat = Chat(1, "", telegramChatId, State.WAIT_APPROVE)

            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(ofType(TelegramCommand::class)) } returns true
                every { handle(ofType(TelegramCommand::class)) } returns State.WAIT_APPROVE
            }
            every { chatService.findByTelegramId(telegramChatId) } returns oldChat
            every { authorizationService.get(any()) } returns null
            every { holder.findHandler(ofType(TelegramCommand::class)) } returns commandHandler
            every { chatService.save(expectedChat) } returns expectedChat

            Then("return command response and change chat state") {


                manager.onUpdate(update)

                verify {
                    chatService.save(expectedChat)
                }

            }
        }
    }

})
