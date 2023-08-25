package com.github.mikesafonov.jira.telegram.service

import com.github.mikesafonov.jira.telegram.dto.FilterSubscription
import com.github.mikesafonov.jira.telegram.dto.SendToAll
import com.github.mikesafonov.jira.telegram.service.telegram.TelegramClient
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class ApiService(
    private val chatService: ChatService,
    private val telegramClient: TelegramClient,
    private val filterSubscriptionService: FilterSubscriptionService
) {

    fun sendToAll(sendToAll: SendToAll) {
        chatService.getAll().forEach {
            telegramClient.sendMarkdownMessage(it.telegramId, sendToAll.message)
        }
    }

    fun addFilterSubscription(filterSubscription: FilterSubscription) {
        chatService.findByJiraId(filterSubscription.jiraLogin)
            ?.let { filterSubscriptionService.addSubscription(filterSubscription.idFilter, it) }
    }

    fun deleteAllFilterSubscriptions(idFilter: Long) {
        filterSubscriptionService.deleteAllSubscriptions(idFilter)
    }
}
