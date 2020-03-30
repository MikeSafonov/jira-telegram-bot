package com.github.mikesafonov.jira.telegram.service.telegram

import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.service.jira.JiraApiService
import com.github.mikesafonov.jira.telegram.service.jira.JiraIssueBrowseLinkService
import com.github.mikesafonov.jira.telegram.service.telegram.handlers.MyIssuesTelegramCommandHandler
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.properties.Gen
import io.kotest.properties.string
import io.mockk.*
import java.net.URI

/**
 * @author Mike Safonov
 */
class MyIssuesTelegramCommandHandlerSpec : BehaviorSpec({
    val jiraApiService = mockk<JiraApiService>()
    val telegramClient = mockk<TelegramClient>()
    val jiraIssueBrowseLinkService = mockk<JiraIssueBrowseLinkService>()

    Given("/my_issues telegram command handler") {
        val handler = MyIssuesTelegramCommandHandler(jiraApiService, telegramClient, jiraIssueBrowseLinkService)
        When("incoming message contain wrong command") {
            val command: TelegramCommand = mockk {
                every { text } returns Gen.string().random().first()
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText(any()) } returns false
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command and wrong state") {
            val command: TelegramCommand = mockk {
                every { text } returns "/my_issues"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.WAIT_APPROVE
                }
                every { isInState(State.INIT) } returns false
                every { isMatchText("/my_issues") } returns true
            }
            Then("isHandle returns false") {
                handler.isHandle(command) shouldBe false
            }
        }

        When("incoming message contain right command") {
            val command: TelegramCommand = mockk {
                every { text } returns "/my_issues"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText("/my_issues") } returns true
                every { authorization } returns mockk()
            }
            Then("isHandle returns true") {
                handler.isHandle(command) shouldBe true
            }
        }


        When("incoming message has no authorization") {
            val telegramChatId = 1L
            val jiraLogin = "jira_login"
            val command: TelegramCommand = mockk {
                every { text } returns "/my_issues"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                    every { telegramId } returns telegramChatId
                    every { jiraId } returns jiraLogin
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText("/my_issues") } returns true
                every { authorization } returns null
            }
            Then("return no authorized request message") {
                every { telegramClient.sendTextMessage(any(), any()) } just Runs

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendTextMessage(telegramChatId, "You must be logged in to use this command. Use the /auth to log in to JIRA")
                }
            }
        }

        When("jira return empty list") {
            val telegramChatId = 1L
            val jiraLogin = "jira_login"
            val command: TelegramCommand = mockk {
                every { text } returns "/my_issues"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                    every { telegramId } returns telegramChatId
                    every { jiraId } returns jiraLogin
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText("/my_issues") } returns true
                every { authorization } returns mockk()
            }
            Then("return no issue found message") {

                every { jiraApiService.getMyIssues(telegramChatId, jiraLogin) } returns emptyList()
                every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(telegramChatId, "No unresolved issues was found")
                }
            }
        }

        When("jira return non empty list") {
            val telegramChatId = 1L
            val jiraLogin = "jira_login"
            val command: TelegramCommand = mockk {
                every { text } returns "/my_issues"
                every { hasText } returns true
                every { chat } returns mockk {
                    every { state } returns State.INIT
                    every { telegramId } returns telegramChatId
                    every { jiraId } returns jiraLogin
                }
                every { isInState(State.INIT) } returns true
                every { isMatchText("/my_issues") } returns true
                every { authorization } returns mockk()
            }
            Then("return created issues") {

                every { jiraApiService.getMyIssues(telegramChatId, jiraLogin) } returns listOf(
                    mockk {
                        every { key } returns "I-1"
                        every { summary } returns "SUMM1"
                        every { self } returns URI("http://my.jira.com/I-1")
                    },
                    mockk {
                        every { key } returns "I-2"
                        every { summary } returns "SUMM2"
                        every { self } returns URI("http://my.jira.com/I-2")
                    }
                )
                every { telegramClient.sendMarkdownMessage(any(), any()) } just Runs
                every { jiraIssueBrowseLinkService.createBrowseLink("I-1", any()) } returns "http://my.jira.com/I-1/browse"
                every { jiraIssueBrowseLinkService.createBrowseLink("I-2", any()) } returns "http://my.jira.com/I-2/browse"

                val expectedMessage = """[I-1](http://my.jira.com/I-1/browse) SUMM1
[I-2](http://my.jira.com/I-2/browse) SUMM2""".trimIndent()

                handler.handle(command) shouldBe State.INIT

                verify {
                    telegramClient.sendMarkdownMessage(telegramChatId, expectedMessage)
                }
            }
        }
    }
})
