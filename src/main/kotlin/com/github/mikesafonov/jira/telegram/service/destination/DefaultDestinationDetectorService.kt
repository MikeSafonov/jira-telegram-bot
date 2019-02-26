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
class DefaultDestinationDetectorService(private val applicationProperties: ApplicationProperties) : DestinationDetectorService {

    /**
     * Find jira logins from [event] to send a telegram message
     */
    override fun findDestinations(event: Event): List<String> {
        if (event.issue != null) {
            val notificationProperties = applicationProperties.notification
            when (event.issueEventTypeName) {
                IssueEventTypeName.ISSUE_COMMENTED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.comment?.author)
                }
                IssueEventTypeName.ISSUE_CREATED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.user)
                }
                IssueEventTypeName.ISSUE_GENERIC -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsers(event.issue)
                }
                IssueEventTypeName.ISSUE_UPDATED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsersWithoutInitiator(event.issue, event.user)
                }
                IssueEventTypeName.ISSUE_COMMENT_EDITED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsers(event.issue)
                }

                IssueEventTypeName.ISSUE_COMMENT_DELETED -> {
                    if (notificationProperties.sendToMe) {
                        return allIssueUsers(event.issue)
                    }
                    return allIssueUsers(event.issue)
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