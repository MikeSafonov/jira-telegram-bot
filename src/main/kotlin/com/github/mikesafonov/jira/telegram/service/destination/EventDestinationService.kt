package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import org.springframework.stereotype.Service

/**
 * @author Mike Safonov
 */
@Service
class EventDestinationService(
    private val detectors: List<DestinationDetectorService>,
    private val applicationProperties: ApplicationProperties
) {

    /**
     * Collect jira logins from [event] for notification
     */
    fun findDestinations(event: Event): Set<String> {
        val notificationProperties = applicationProperties.notification
        val logins = collectLogins(event)
        return if (notificationProperties.sendToMe) {
            logins
        } else {
            val initiator = getInitiatorLogin(event)
            logins.minus(initiator)
        }
    }

    private fun collectLogins(event: Event): Set<String> {
        val logins = HashSet<String>()
        detectors.forEach {
            val value = it.findDestinations(event)
            logins.addAll(value)
        }
        return logins
    }

    private fun getInitiatorLogin(event: Event): String {
        return when (event.issueEventTypeName!!) {
            IssueEventTypeName.ISSUE_COMMENTED -> {
                event.comment?.author?.name!!
            }
            IssueEventTypeName.ISSUE_CREATED, IssueEventTypeName.ISSUE_GENERIC,
            IssueEventTypeName.ISSUE_UPDATED, IssueEventTypeName.ISSUE_COMMENT_EDITED,
            IssueEventTypeName.ISSUE_COMMENT_DELETED, IssueEventTypeName.ISSUE_ASSIGNED -> {
                event.user?.name!!
            }
        }
    }
}
