package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.RawTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateResolverService
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.properties.Gen
import io.kotest.properties.int
import io.kotest.properties.long
import io.kotest.properties.string
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

/**
 * @author Mike Safonov
 */
class EventServiceSpec : BehaviorSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        val telegramClient = mockk<TelegramClient>()
        val templateService = mockk<TemplateService>()
        val destinationDetectorService = mockk<DestinationDetectorService>()
        val parametersBuilderService = mockk<ParametersBuilderService>()
        val chatRepository = mockk<ChatRepository>()
        val templateResolverService = mockk<TemplateResolverService>()
        val eventService =
            EventService(
                templateResolverService,
                templateService,
                destinationDetectorService,
                parametersBuilderService,
                telegramClient,
                chatRepository
            )

        Given("Event service") {
            When("Incoming event is not issue event") {
                Then("Ignore event") {
                    val events = WebHookEvent.values()
                        .filter {
                            !it.isIssueEvent()
                        }
                        .map {
                            EventGen().generateOne(
                                webHookEvent = it
                            )
                        }
                    events.forEach {
                        eventService.handle(it)
                        verify {
                            listOf(
                                telegramClient,
                                templateService,
                                destinationDetectorService,
                                parametersBuilderService,
                                chatRepository
                            ) wasNot Called
                        }
                    }
                }
            }

            When("Incoming issue event without issueEventTypeName") {
                Then("Ignore event") {
                    val events = listOf(
                        EventGen().generateOne(
                            webHookEvent = WebHookEvent.JIRA_ISSUE_CREATED,
                            issueEventTypeName = null
                        ),
                        EventGen().generateOne(
                            webHookEvent = WebHookEvent.JIRA_ISSUE_UPDATED,
                            issueEventTypeName = null
                        ),
                        EventGen().generateOne(
                            webHookEvent = WebHookEvent.JIRA_ISSUE_DELETED,
                            issueEventTypeName = null
                        )
                    )
                    events.forEach {
                        eventService.handle(it)
                        verify {
                            listOf(
                                telegramClient,
                                templateService,
                                destinationDetectorService,
                                parametersBuilderService,
                                chatRepository
                            ) wasNot Called
                        }
                    }
                }
            }
            When("Incoming issue event without destination") {
                Then("Ignore event") {
                    randomIssueEvents().forEach {
                        every { destinationDetectorService.findDestinations(it) } returns emptyList()

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient,
                                templateService,
                                parametersBuilderService,
                                chatRepository
                            ) wasNot Called
                        }
                    }
                }
            }

            When("No template for incoming issue event ") {
                Then("Ignore event") {
                    randomIssueEvents().forEach {
                        val parameters = mapOf("event" to it)
                        every { destinationDetectorService.findDestinations(it) } returns listOf(Gen.string().random().first())
                        every { templateResolverService.resolve(it, parameters) } returns null
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient,
                                chatRepository
                            ) wasNot Called
                            parametersBuilderService.buildTemplateParameters(it)
                            destinationDetectorService.findDestinations(it)
                            templateResolverService.resolve(it, parameters)
                        }
                    }

                }
            }

            When("No chat for incoming issue event") {
                Then("Ignore event") {
                    randomIssueEvents().forEach {
                        val destinationLogin = Gen.string().random().first()
                        val parameters = mapOf("event" to it)
                        val rawTemplate =
                            RawTemplate(Gen.string().random().first(), Gen.string().random().first(), emptyMap())
                        val template = CompiledTemplate(
                            Gen.string().random().first(),
                            true
                        )
                        every { destinationDetectorService.findDestinations(it) } returns listOf(destinationLogin)
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters
                        every { templateResolverService.resolve(it, parameters) } returns rawTemplate
                        every { templateService.buildMessage(rawTemplate) } returns template
                        every { chatRepository.findByJiraId(destinationLogin) } returns null

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient
                            ) wasNot Called
                            parametersBuilderService.buildTemplateParameters(it)
                            destinationDetectorService.findDestinations(it)
                            chatRepository.findByJiraId(destinationLogin)
                            templateResolverService.resolve(it, parameters)
                            templateService.buildMessage(rawTemplate)
                        }
                    }
                }
            }

            When("") {
                Then("Send telegram event") {
                    randomIssueEvents().forEach {
                        val destinationLogin = Gen.string().random().first()
                        val telegramId = Gen.long().random().first()
                        val parameters = mapOf("event" to it)
                        val rawTemplate =
                            RawTemplate(Gen.string().random().first(), Gen.string().random().first(), emptyMap())
                        val template = CompiledTemplate(Gen.string().random().first(), true)
                        every { destinationDetectorService.findDestinations(it) } returns listOf(destinationLogin)
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters
                        every { templateResolverService.resolve(it, parameters) } returns rawTemplate
                        every { templateService.buildMessage(rawTemplate) } returns template
                        every { chatRepository.findByJiraId(destinationLogin) } returns Chat(
                            Gen.int().random().first(),
                            destinationLogin,
                            telegramId,
                            State.INIT
                        )
                        every { telegramClient.sendMarkdownMessage(any(), any()) } answers {}

                        eventService.handle(it)

                        verify {
                            parametersBuilderService.buildTemplateParameters(it)
                            destinationDetectorService.findDestinations(it)
                            telegramClient.sendMarkdownMessage(telegramId, template.message)
                            chatRepository.findByJiraId(destinationLogin)
                            templateResolverService.resolve(it, parameters)
                            templateService.buildMessage(rawTemplate)
                        }
                    }
                }
            }
        }
    }

    private fun randomIssueEvents(): List<Event> {
        return WebHookEvent.issueEvents().map {
            EventGen().generateOne(
                webHookEvent = it
            )
        }
    }
}
