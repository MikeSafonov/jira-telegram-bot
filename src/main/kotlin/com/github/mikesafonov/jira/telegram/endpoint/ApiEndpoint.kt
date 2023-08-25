package com.github.mikesafonov.jira.telegram.endpoint

import com.github.mikesafonov.jira.telegram.dto.FilterSubscription
import com.github.mikesafonov.jira.telegram.dto.SendToAll
import com.github.mikesafonov.jira.telegram.service.ApiService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author Mike Safonov
 */
@RestController
@RequestMapping("/api")
class ApiEndpoint(private val apiService: ApiService) {

    @PostMapping("/send/all")
    fun sendToAll(@RequestBody sendToAll: SendToAll) {
        apiService.sendToAll(sendToAll)
    }

    @PostMapping("/filters-subscriptions")
    fun addFilterSubscription(@RequestBody filterSubscription: FilterSubscription) {
        apiService.addFilterSubscription(filterSubscription)
    }

    @DeleteMapping("/filters-subscriptions")
    fun addFilterSubscription(@RequestParam idFilter: Long) {
        apiService.deleteAllFilterSubscriptions(idFilter)
    }
}
