package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.ChatTagRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.empty
import com.github.mikesafonov.jira.telegram.negative
import com.github.mikesafonov.jira.telegram.notBlank
import com.github.mikesafonov.jira.telegram.positive
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.random.Random

class ChatServiceSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>(relaxed = true)
    val chatTagRepository = mockk<ChatTagRepository>(relaxed = true)
    Given("chat service") {
        val chatService = ChatService(chatRepository, chatTagRepository)

        When("jiraLogin is empty") {
            val jiraLogin = Arb.string().empty()
            val telegramId = Arb.long().next()
            Then("throw exception") {
                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Wrong command args: jiraLogin must not be blank"
            }
        }

        When("jiraLogin exists in database") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = Arb.long().next()
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.findByJiraId(jiraLogin) } returns chat

            Then("throw exception") {
                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Jira login $jiraLogin already exist"
            }
        }

        When("telegramId is negative") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = Random.negative()
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            Then("throw exception") {

                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Wrong command args: telegramId must be a positive number"
            }
        }

        When("telegramId is zero") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = 0L
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            Then("throw exception") {

                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Wrong command args: telegramId must be a positive number"
            }
        }

        When("telegramId exists in database") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = Random.positive()
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            every { chatRepository.findByTelegramId(telegramId) } returns chat

            Then("throw exception") {
                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Telegram id $telegramId already exist"
            }
        }

        When("jiraLogin and telegramId is ok") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = Random.positive()
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            every { chatRepository.findByTelegramId(telegramId) } returns null
            every { chatRepository.save(chat) } returns chat

            Then("should save new chat") {
                chatService.addNewChat(jiraLogin, telegramId)

                verify {
                    chatRepository.save(chat)
                }
            }
        }

        When("delete and chat not exist") {
            val jiraLogin = Arb.string().notBlank()
            every { chatRepository.findByJiraId(jiraLogin) } returns null

            Then("should do nothing") {
                chatService.deleteByJiraId(jiraLogin)
                verify(exactly = 0) {chatRepository.deleteById(any())}
                verify(exactly = 0) {chatTagRepository.deleteByIdChat(any())}
            }
        }
        When("delete and chat exist") {
            val jiraLogin = Arb.string().notBlank()
            val telegramId = Random.positive()
            val id = 10
            val chat = Chat(id, jiraLogin, telegramId, State.INIT)
            every { chatRepository.findByJiraId(jiraLogin) } returns chat

            Then("should delete chat and chats tags") {
                chatService.deleteByJiraId(jiraLogin)
                verify(exactly = 1) {chatRepository.deleteById(id)}
                verify(exactly = 1) {chatTagRepository.deleteByIdChat(id)}
            }
        }
    }
})
