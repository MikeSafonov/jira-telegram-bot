package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.dao.ChatRepository
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.service.EventService
import com.github.mikesafonov.jira.telegram.service.destination.DestinationDetectorService
import com.github.mikesafonov.jira.telegram.service.parameters.ParametersBuilderService
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramBot
import com.github.mikesafonov.jira.telegram.service.templates.TemplateService
import io.kotlintest.specs.BehaviorSpec
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify

/**
 * @author Mike Safonov
 */
class EventServiceSpec : BehaviorSpec({
    val telegramBot = mockk<TelegramBot>()
    val templateService = mockk<TemplateService>()
    val destinationDetectorService = mockk<DestinationDetectorService>()
    val parametersBuilderService = mockk<ParametersBuilderService>()
    val chatRepository = mockk<ChatRepository>()
    val eventService =
        EventService(templateService, destinationDetectorService, parametersBuilderService, telegramBot, chatRepository)


    Given("Event service") {
        When("Event is not issue event") {
            val event = Event(WebHookEvent.COMMENT_CREATED, null, 10000L, null, null, null, null)
            Then("Should ignore this event") {
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
        }


        When("Issue event without issueEventTypeName"){
            val event = Event(WebHookEvent.JIRA_ISSUE_CREATED, null, 10000L, null, null, null, null)
            Then("Should ignore this event") {
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

        }
    }
})