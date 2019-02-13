package com.github.mikesafonov.jira.telegram.endpoint

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.EventService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EventEndpoint(private val eventService: EventService) {

    @PostMapping("/")
    fun handleEvent(@RequestBody event: Event) {
        eventService.handle(event)
    }

}