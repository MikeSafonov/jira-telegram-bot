package com.github.mikesafonov.jira.telegram

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.WebHookEvent
import com.github.mikesafonov.jira.telegram.service.destination.DefaultDestinationDetectorService
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.specs.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

/**
 * @author Mike Safonov
 */
class DefaultDestinationDetectorServiceSpec : BehaviorSpec({

    val applicationProperties = mockk<ApplicationProperties>()
    lateinit var defaultDestinationDetectorService: DefaultDestinationDetectorService

    Given("Destination service with flag sendToMe = true") {
        every { applicationProperties.notification.sendToMe } returns true
        defaultDestinationDetectorService = DefaultDestinationDetectorService(applicationProperties)

        When("Event without issue field") {
            val event = Event(WebHookEvent.COMMENT_CREATED, null, 10000L, null, null, null, null)
            Then("Return empty list") {
                defaultDestinationDetectorService.findDestinations(event) shouldHaveSize 0

            }
        }
    }

//    Given("Destination service with flag sendToMe = false") {
//        every { applicationProperties.notification.sendToMe } returns false
//        defaultDestinationDetectorService = DefaultDestinationDetectorService(applicationProperties)
//    }

})