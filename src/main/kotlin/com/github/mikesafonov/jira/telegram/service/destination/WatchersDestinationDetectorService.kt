package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.conditional.ConditionalOnJiraWatchers
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.service.jira.JiraWatchersLoader
import org.springframework.stereotype.Service

/**
 * Collects watchers of events issue
 * @author Mike Safonov
 */
@Service
@ConditionalOnJiraWatchers
class WatchersDestinationDetectorService(
    private val watchersLoader: JiraWatchersLoader
) : DestinationDetectorService {

    /**
     * Find jira logins from [event] to send a telegram message
     */
    override fun findDestinations(event: Event): List<String> {
        val watcherSelf = event.issue?.fields?.watches?.self
        return getWatchers(watcherSelf)
    }


    private fun getWatchers(self: String?): List<String> {
        if (self != null) {
            return watchersLoader.getWatchers(self)
        }
        return emptyList()
    }
}
