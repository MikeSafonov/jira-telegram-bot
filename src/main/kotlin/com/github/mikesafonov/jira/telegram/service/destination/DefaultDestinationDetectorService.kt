package com.github.mikesafonov.jira.telegram.service.destination

import com.github.mikesafonov.jira.telegram.dto.Event
import com.github.mikesafonov.jira.telegram.dto.Issue
import org.springframework.stereotype.Service

/**
 * Default implementation of [DestinationDetectorService]. Collects issue creator, reporter and assignee names
 * @author Mike Safonov
 */
@Service
class DefaultDestinationDetectorService : DestinationDetectorService {

    /**
     * Find jira logins from [event] to send a telegram message
     */
    override fun findDestinations(event: Event): List<String> {
        return if (event.issue != null && event.issueEventTypeName != null) {
            allIssueUsers(event.issue)
        } else {
            emptyList()
        }
    }

    /**
     * Collect **creator**, **reporter** and **assignee** logins from [issue] to list, ignoring *null* values
     * @param issue jira issue
     * @return list of logins
     */
    protected fun allIssueUsers(issue: Issue): List<String> {
        return listOfNotNull(issue.creatorName, issue.reporterName, issue.assigneeName)
    }
}
