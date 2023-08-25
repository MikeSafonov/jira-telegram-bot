package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.FilterSubscriptionService
import com.github.mikesafonov.jira.telegram.service.jira.JiraApiService
import org.springframework.stereotype.Service

@Service
class FilterSubscriptionDestinationDetector(
    val filterSubscriptionService: FilterSubscriptionService,
    val jiraApiService: JiraApiService
) :
    DestinationDetectorService {
    override fun findDestinations(event: Event): List<String> {
        if (!event.isIssueEvent || event.issue == null) return emptyList()

        return filterSubscriptionService.getAll()
            .mapNotNull {
                if (jiraApiService.getIssueByFilter(it.id.chat.telegramId, it.id.idFilter)
                        .any { issue -> issue.id == event.issue.id }
                ) {
                    return@mapNotNull it.id.chat.jiraId
                }
                return@mapNotNull null
            }
    }
}