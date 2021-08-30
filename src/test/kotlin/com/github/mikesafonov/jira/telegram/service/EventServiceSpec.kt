package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.State
import com.github.mikesafonov.jira.telegram.dao.TemplateParseMode
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.service.destination.EventDestinationService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.RawTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateResolverService
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
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
        val destinationDetectorService = mockk<EventDestinationService>()
        val parametersBuilderService = mockk<ParametersBuilderService>()
        val chatService = mockk<ChatService>()
        val templateResolverService = mockk<TemplateResolverService>()
        val eventService =
            EventService(
                templateResolverService,
                templateService,
                destinationDetectorService,
                parametersBuilderService,
                telegramClient,
                chatService
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
                                chatService
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
                                chatService
                            ) wasNot Called
                        }
                    }
                }
            }
            When("Incoming issue event without destination") {
                Then("Ignore event") {
                    randomIssueEvents().forEach {
                        every { destinationDetectorService.findDestinations(it) } returns emptySet()

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient,
                                templateService,
                                parametersBuilderService,
                                chatService
                            ) wasNot Called
                        }
                    }
                }
            }

            When("No template for incoming issue event ") {
                Then("Ignore event") {
                    randomIssueEvents().forEach {
                        val parameters = mutableMapOf<String, Any>("event" to it)
                        every { destinationDetectorService.findDestinations(it) } returns setOf(Arb.string().next())
                        every { templateResolverService.resolve(it, parameters) } returns null
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient,
                                chatService
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
                        val destinationLogin = Arb.string().next()
                        val parameters = mutableMapOf<String, Any>("event" to it)
                        val rawTemplate =
                            RawTemplate(Arb.string().next(), Arb.string().next(), mutableMapOf(),
                                Arb.enum<TemplateParseMode>().next())
                        val template = CompiledTemplate(
                            Arb.string().next(),
                            true
                        )
                        every { destinationDetectorService.findDestinations(it) } returns setOf(destinationLogin)
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters
                        every { templateResolverService.resolve(it, parameters) } returns rawTemplate
                        every { templateService.buildMessage(rawTemplate) } returns template
                        every { chatService.findByJiraId(destinationLogin) } returns null

                        eventService.handle(it)

                        verify {
                            listOf(
                                telegramClient
                            ) wasNot Called
                            parametersBuilderService.buildTemplateParameters(it)
                            destinationDetectorService.findDestinations(it)
                            chatService.findByJiraId(destinationLogin)
                            templateResolverService.resolve(it, parameters)
                            templateService.buildMessage(rawTemplate)
                        }
                    }
                }
            }

            When("Events is ok") {
                Then("Send telegram event") {
                    randomIssueEvents().forEach {
                        val destinationLogin = Arb.string().next()
                        val telegramId = Arb.long().next()
                        val parameters = mutableMapOf<String, Any>("event" to it)
                        val rawTemplate =
                            RawTemplate(Arb.string().next(), Arb.string().next(), mutableMapOf(),
                                Arb.enum<TemplateParseMode>().next())
                        val template = CompiledTemplate(Arb.string().next(), true)
                        every { destinationDetectorService.findDestinations(it) } returns setOf(destinationLogin)
                        every { parametersBuilderService.buildTemplateParameters(it) } returns parameters
                        every { templateResolverService.resolve(it, parameters) } returns rawTemplate
                        every { templateService.buildMessage(rawTemplate) } returns template
                        every { chatService.findByJiraId(destinationLogin) } returns Chat(
                            Arb.int().next(),
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
                            chatService.findByJiraId(destinationLogin)
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
