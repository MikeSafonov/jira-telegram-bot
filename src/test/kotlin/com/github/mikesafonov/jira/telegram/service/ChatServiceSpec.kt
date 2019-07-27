package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.empty
import com.github.mikesafonov.jira.telegram.negative
import com.github.mikesafonov.jira.telegram.notBlank
import com.github.mikesafonov.jira.telegram.positive
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowExactly
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.random.Random

class ChatServiceSpec : BehaviorSpec({
    val chatRepository = mockk<ChatRepository>()
    Given("chat service") {
        val chatService = ChatService(chatRepository)

        When("jiraLogin is empty") {
            val jiraLogin = Gen.string().empty()
            val telegramId = Gen.long().random().first()
            Then("throw exception") {
                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Wrong command args: jiraLogin must not be blank"
            }
        }

        When("jiraLogin exists in database") {
            val jiraLogin = Gen.string().notBlank()
            val telegramId = Gen.long().random().first()
            val chat = Chat(null, jiraLogin, telegramId, State.INIT)
            every { chatRepository.findByJiraId(jiraLogin) } returns chat

            Then("throw exception") {
                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Jira login $jiraLogin already exist"
            }
        }

        When("telegramId is invalid") {
            val jiraLogin = Gen.string().notBlank()
            val telegramId = Random.negative()
            every { chatRepository.findByJiraId(jiraLogin) } returns null
            Then("throw exception") {

                val exception =
                    shouldThrowExactly<AddChatException> { chatService.addNewChat(jiraLogin, telegramId) }
                exception.message shouldBe "Wrong command args: telegramId must be a positive number"
            }
        }

        When("telegramId exists in database") {
            val jiraLogin = Gen.string().notBlank()
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
            val jiraLogin = Gen.string().notBlank()
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
    }
})