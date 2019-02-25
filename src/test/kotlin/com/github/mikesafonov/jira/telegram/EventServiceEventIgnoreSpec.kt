package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.dao.Chat
import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.generators.EventGen
import com.github.mikesafonov.jira.telegram.service.EventService
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramBot
import com.github.mikesafonov.jira.telegram.service.templates.CompiledTemplate
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import io.kotlintest.IsolationMode
import io.kotlintest.properties.Gen
import io.kotlintest.specs.StringSpec
import io.mockk.*

/**
 * @author Mike Safonov
 */
class EventServiceEventIgnoreSpec : StringSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.SingleInstance

    val telegramBot = mockk<TelegramBot>(relaxed = true)
    val templateService = mockk<TemplateService>(relaxed = true)
    val destinationDetectorService = mockk<DestinationDetectorService>(relaxed = true)
    val parametersBuilderService = mockk<ParametersBuilderService>(relaxed = true)
    val chatRepository = mockk<ChatRepository>(relaxed = true)
    val eventService =
        EventService(
            templateService,
            destinationDetectorService,
            parametersBuilderService,
            telegramBot,
            chatRepository
        )

    init {

        "Should ignore event because it not a issue event"{

            val events = listOf(
                EventGen().generateOne(
                    webHookEvent = WebHookEvent.COMMENT_CREATED
                ),
                EventGen().generateOne(
                    webHookEvent = WebHookEvent.COMMENT_DELETED
                ),
                EventGen().generateOne(
                    webHookEvent = WebHookEvent.COMMENT_UPDATED
                )
            )
            events.forEach {
                eventService.handle(it)
                verify {
                    listOf(
                        telegramBot,
                        templateService,
                        destinationDetectorService,
                        parametersBuilderService,
                        chatRepository
                    ) wasNot Called
                }
            }
        }

        "Should ignore issue event without issueEventTypeName"{

            val event = EventGen().generateOne(issueEventTypeName = null)

            eventService.handle(event)
            verify {
                listOf(
                    telegramBot,
                    templateService,
                    destinationDetectorService,
                    parametersBuilderService,
                    chatRepository
                ) wasNot Called
            }
        }

        "Should ignore issue event because no destinations"{


            val event = EventGen.generateDefault()
            every { destinationDetectorService.findDestinations(event) } returns emptyList()

            eventService.handle(event)

            verify {
                listOf(
                    telegramBot,
                    templateService,
                    parametersBuilderService,
                    chatRepository
                ) wasNot Called
            }
            clearMocks(destinationDetectorService)
        }

        "Should ignore event because no template was found"{
            every { destinationDetectorService.findDestinations(any()) } returns listOf(Gen.string().random().first())
            every { templateService.buildMessage(any(), any()) } returns null
            val event = EventGen.generateDefault()
            val parameters = mapOf("event" to event)
            every { parametersBuilderService.buildTemplateParameters(event) } returns parameters
            eventService.handle(event)
            verify {
                listOf(
                    telegramBot,
                    chatRepository
                ) wasNot Called
                parametersBuilderService.buildTemplateParameters(event)
                destinationDetectorService.findDestinations(event)
                templateService.buildMessage(event, parameters)
            }
            clearMocks(destinationDetectorService, templateService, parametersBuilderService)
        }

        "Should be ignored because no chat was found"{
            val event = EventGen.generateDefault()
            val destinationLogin = Gen.string().random().first()
            val parameters = mapOf("event" to event)
            val template = CompiledTemplate(
                Gen.string().random().first(),
                true
            )
            every { destinationDetectorService.findDestinations(event) } returns listOf(destinationLogin)
            every { parametersBuilderService.buildTemplateParameters(event) } returns parameters
            every { templateService.buildMessage(event, parameters) } returns template
            every { chatRepository.findByJiraId(destinationLogin) } returns null
            eventService.handle(event)

            verify {
                listOf(
                    telegramBot
                ) wasNot Called
                parametersBuilderService.buildTemplateParameters(event)
                destinationDetectorService.findDestinations(event)
                chatRepository.findByJiraId(destinationLogin)
                templateService.buildMessage(event, parameters)
            }
            clearMocks(destinationDetectorService, templateService, parametersBuilderService,chatRepository)
        }

        "Should send telegram message"{
            val event = EventGen.generateDefault()
            val destinationLogin = Gen.string().random().first()
            val telegramId = Gen.long().random().first()
            val parameters = mapOf("event" to event)
            val template = CompiledTemplate(Gen.string().random().first(), true)
            every { destinationDetectorService.findDestinations(event) } returns listOf(destinationLogin)
            every { parametersBuilderService.buildTemplateParameters(event) } returns parameters
            every { templateService.buildMessage(event, parameters) } returns template
            every { chatRepository.findByJiraId(destinationLogin) } returns Chat(
                Gen.int().random().first(),
                destinationLogin,
                telegramId
            )
            eventService.handle(event)

            verify {
                parametersBuilderService.buildTemplateParameters(event)
                destinationDetectorService.findDestinations(event)
                telegramBot.sendMarkdownMessage(telegramId, template.message)
                chatRepository.findByJiraId(destinationLogin)
                templateService.buildMessage(event, parameters)
            }
            clearMocks(destinationDetectorService, templateService, parametersBuilderService,chatRepository)
        }
    }
}