package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.config.ApplicationProperties
import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
import com.github.mikesafonov.jira.telegram.dto.IssueEventTypeName
import com.github.mikesafonov.jira.telegram.dto.User

/**
 * Default implementation of [DestinationDetectorService]
 * @author Mike Safonov
 */
class DefaultDestinationDetectorService(private val applicationProperties: ApplicationProperties) :
    DestinationDetectorService {

    /**
     * Find jira logins from [event] to send a telegram message
     */
    override fun findDestinations(event: Event): List<String> {
        val notificationProperties = applicationProperties.notification
        return if (notificationProperties.sendToMe) {
            if (event.issue != null && event.issueEventTypeName != null) {
                allIssueUsers(event.issue)
            } else {
                emptyList()
            }
        } else {
            requiredDestinations(event)
        }
    }

    private fun requiredDestinations(event: Event): List<String> {
        if (event.issue != null) {
            when (event.issueEventTypeName) {
                IssueEventTypeName.ISSUE_COMMENTED -> {
                    return allIssueUsersWithoutInitiator(event.issue, event.comment?.author)
                }
                IssueEventTypeName.ISSUE_CREATED, IssueEventTypeName.ISSUE_GENERIC,
                IssueEventTypeName.ISSUE_UPDATED, IssueEventTypeName.ISSUE_COMMENT_EDITED,
                IssueEventTypeName.ISSUE_COMMENT_DELETED -> {
                    return allIssueUsersWithoutInitiator(event.issue, event.user)
                }
            }
        }
        return emptyList()
    }


    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values
     * @param issue jira issue
     * @return list of logins
     */
    private fun allIssueUsers(issue: Issue): List<String> {
        return listOfNotNull(issue.creatorName, issue.reporterName, issue.assigneeName)
            .distinct()
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values and filtered
     * by [initiator] login if present.
     * @param issue jira issue
     * @param initiator user who fired this issues event
     * @return list of logins
     */
    private fun allIssueUsersWithoutInitiator(issue: Issue, initiator: User?): List<String> {
        return if (initiator == null) {
            allIssueUsers(issue)
        } else {
            allIssueUsers(issue)
                .filter { it != initiator.name }
        }
    }
}