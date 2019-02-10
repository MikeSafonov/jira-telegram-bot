package com.github.mikesafonov.jira.telegram

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(private val eventService: EventService) {

    @PostMapping("event")
    fun handleEvent(@RequestBody event: Event) {
        eventService.handle(event)
    }
}