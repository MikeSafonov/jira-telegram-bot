package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.AuthorizationRepository
import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.TelegramCommandHandler
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*

/**
 * @author Mike Safonov
 */
class TelegramUpdateManagerSpec : BehaviorSpec({

    val chatRepository = mockk<ChatRepository>()
    val holder = mockk<TelegramHandlersHolder>()
    val authorizationRepository = mockk<AuthorizationRepository>()

    Given("telegram update manager") {
        val manager = TelegramUpdateManager(chatRepository, authorizationRepository, holder)

        When("handler exist and no need to change chat state") {
            val telegramChatId = 1L
            val update: Update = mockk {
                every { hasMessage() } returns true
                every { message } returns mockk {
                    every { hasText() } returns true
                    every { chatId } returns telegramChatId
                }
            }

            every { chatRepository.findByTelegramId(telegramChatId) } returns mockk {
                every { state } returns State.INIT
            }
            every { authorizationRepository.findById(any()) } returns Optional.empty()
            val commandHandler = mockk<TelegramCommandHandler> {
                every { isHandle(ofType(TelegramCommand::class)) } returns true
                every { handle(ofType(TelegramCommand::class)) } returns State.INIT
            }

            every { holder.findHandler(ofType(TelegramCommand::class)) } returns commandHandler

            Then("return command response and not change chat state") {

                manager.onUpdate(update)


                verify {
                    chatRepository.save(ofType(Chat::class)) wasNot Called
                }

            }
        }

        When("handler exist and  need to change chat state") {
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
            every { chatRepository.findByTelegramId(telegramChatId) } returns oldChat
            every { authorizationRepository.findById(any()) } returns Optional.empty()
            every { holder.findHandler(ofType(TelegramCommand::class)) } returns commandHandler
            every { chatRepository.save(expectedChat) } returns expectedChat

            Then("return command response and change chat state") {


                manager.onUpdate(update)

                verify {
                    chatRepository.save(expectedChat)
                }

            }
        }
    }

})
