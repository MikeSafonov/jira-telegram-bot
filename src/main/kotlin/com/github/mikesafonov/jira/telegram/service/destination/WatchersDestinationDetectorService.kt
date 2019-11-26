package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.Comment
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.User
import com.github.mikesafonov.jira.telegram.service.jira.JiraWatchersLoader

/**
 * Default implementation of [DestinationDetectorService]
 * @author Mike Safonov
 */
class WatchersDestinationDetectorService(
    private val applicationProperties: ApplicationProperties,
    private val watchersLoader: JiraWatchersLoader
) :
    BaseDestinationDetectorService() {

    /**
     * Find jira logins from [event] to send a telegram message
     */
    override fun findDestinations(event: Event): List<String> {
        val notificationProperties = applicationProperties.notification
        return if (notificationProperties.sendToMe) {
            if (event.issue != null && event.issueEventTypeName != null) {
                allIssueUsers(event.issue).plus(getMentionsFromComment(event.comment))
                    .plus(getWatchers(event.issue.fields.watches?.self))
                    .distinct()
            } else {
                emptyList()
            }
        } else {
            requiredDestinations(event)
        }
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values and filtered
     * by [initiator] login if present.
     * @param issue jira issue
     * @param initiator user who fired this issues event
     * @return list of logins
     */
    override fun allIssueUsersWithoutInitiator(issue: Issue, initiator: User?, comment: Comment?): List<String> {
        val users = allIssueUsers(issue)
            .plus(getMentionsFromComment(comment))
            .plus(getWatchers(issue.fields.watches?.self))

        return if (initiator == null) {
            users
                .distinct()
        } else {
            users
                .filter { it != initiator.name }
                .distinct()
        }
    }

    private fun getWatchers(self: String?): List<String> {
        if (self != null) {
            return watchersLoader.getWatchers(self)
        }
        return emptyList()
    }
}
