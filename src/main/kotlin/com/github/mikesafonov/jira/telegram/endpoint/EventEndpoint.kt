package com.github.mikesafonov.jira.telegram.endpoint

import com.github.mikesafonov.jira.telegram.config.prometheus.JiraEventCounter
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.EventService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * @author Mike Safonov
 */
@RestController
class EventEndpoint(
    private val eventService: EventService,
    private val jiraEventCounter: JiraEventCounter
) {

    @PostMapping("/")
    fun handleEvent(@RequestBody event: Event) {
        jiraEventCounter.incrementEvent()
        try {
            eventService.handle(event)
        } catch (e: Exception) {
            jiraEventCounter.incrementError()
            throw e
        }
    }

}